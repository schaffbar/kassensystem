import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TokenAssignment } from './token.model';

const TOKEN_API_URL = 'http://localhost:8080/api/v1/token-assignments';

@Injectable({ providedIn: 'root' })
export class TokenService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-token', //
    }),
  };

  getTokenAssignmetByUser(userId: string): Observable<TokenAssignment> {
    return this.http
      .get<TokenAssignment[]>(`${TOKEN_API_URL}?customer-id=${userId}`)
      .pipe(map((assignments) => assignments[0] || null));
  }

  assignToken(userId: string): Observable<void> {
    const body = {
      customerId: userId,
      tokenType: 'FIXED',
    };

    return this.http.post<void>(TOKEN_API_URL, body, this.httpOptions);
  }

  assignTemporaryToken(userId: string): Observable<void> {
    const body = {
      customerId: userId,
      tokenType: 'TEMPORARY',
    };

    return this.http.post<void>(TOKEN_API_URL, body, this.httpOptions);
  }
}
