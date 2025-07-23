import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { User } from './user.model';

const USERS_API_URL = 'http://localhost:8080/api/v1/customers';

@Injectable({ providedIn: 'root' })
export class UsersService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-token', //
    }),
  };

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(USERS_API_URL);
  }

  getUsersByFilter(filter: string): Observable<User[]> {
    return this.http.get<User[]>(`${USERS_API_URL}?filter=${filter}`);
  }

  getUser(id: string): Observable<User> {
    return this.http.get<User>(`${USERS_API_URL}/${id}`);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(USERS_API_URL, user, this.httpOptions);
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${USERS_API_URL}/${id}`);
  }
}
