import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { ActiveUser } from './dashboard.model';

const DASHBOARD_API_URL = `${environment.apiBaseUrl}/api/v1/workshop-dashboard`;

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  // --------------------------------------------------------------------------
  // queries

  getActiveUsers(): Observable<ActiveUser[]> {
    return this.http.get<ActiveUser[]>(`${DASHBOARD_API_URL}/active-users`);
  }
}
