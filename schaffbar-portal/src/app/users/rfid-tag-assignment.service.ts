import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { RfidTagAssignment } from './rfid-tag-assignment.model';

const RFID_TAG_ASSIGNMENT_API_URL = `${environment.apiBaseUrl}/api/v1/rfid-tag-assignments`;

@Injectable({ providedIn: 'root' })
export class RfidTagAssignmentService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  getRfidTagAssignmetByUser(userId: string): Observable<RfidTagAssignment> {
    return this.http
      .get<RfidTagAssignment[]>(`${RFID_TAG_ASSIGNMENT_API_URL}?customer-id=${userId}`)
      .pipe(map((assignments) => assignments[0] || null));
  }

  assignFixedRfidTag(userId: string): Observable<void> {
    const body = {
      customerId: userId,
      assignmentType: 'FIXED',
    };

    return this.http.post<void>(RFID_TAG_ASSIGNMENT_API_URL, body, this.httpOptions);
  }

  assignTemporaryRfidTag(userId: string): Observable<void> {
    const body = {
      customerId: userId,
      assignmentType: 'TEMPORARY',
    };

    return this.http.post<void>(RFID_TAG_ASSIGNMENT_API_URL, body, this.httpOptions);
  }

  unassignRfidTag(userId: string): Observable<void> {
    const body = {
      customerId: userId,
    };

    return this.http.put<void>(`${RFID_TAG_ASSIGNMENT_API_URL}/unassign`, body, this.httpOptions);
  }
}
