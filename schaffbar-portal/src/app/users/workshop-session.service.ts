import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { WorkshopSession } from './workshop-session.model';

const WORKSHOP_SESSION_API_URL = `${environment.apiBaseUrl}/api/v1/workshop-sessions`;

@Injectable({ providedIn: 'root' })
export class WorkshopSessionService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  // --------------------------------------------------------------------------
  // queries

  getActiveWorkshopSession(userId: string): Observable<WorkshopSession> {
    return this.http.get<WorkshopSession>(`${WORKSHOP_SESSION_API_URL}/${userId}/active`);
  }

  // --------------------------------------------------------------------------
  // commands

  closeWorkshopSession(userId: string): Observable<void> {
    return this.http.put<void>(`${WORKSHOP_SESSION_API_URL}/${userId}/close`, this.httpOptions);
  }
}
