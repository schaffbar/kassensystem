import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { User, UserAddress } from './user.model';

const USERS_API_URL = `${environment.apiBaseUrl}/api/v1/customers`;

@Injectable({ providedIn: 'root' })
export class UsersService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  // --------------------------------------------------------------------------
  // queries

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(USERS_API_URL);
  }

  getUsersByFilter(filter: string): Observable<User[]> {
    return this.http.get<User[]>(`${USERS_API_URL}?filter=${filter}`);
  }

  getUser(id: string): Observable<User> {
    return this.http.get<User>(`${USERS_API_URL}/${id}`);
  }

  // --------------------------------------------------------------------------
  // commands

  createUser(user: User): Observable<User> {
    return this.http.post<User>(USERS_API_URL, user, this.httpOptions);
  }

  updateUserAddress(id: string, address: UserAddress): Observable<void> {
    return this.http.put<void>(`${USERS_API_URL}/${id}/address`, address, this.httpOptions);
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${USERS_API_URL}/${id}`);
  }
}
