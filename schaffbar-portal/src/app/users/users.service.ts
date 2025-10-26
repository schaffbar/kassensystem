import { DatePipe } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { User, UserAddress } from './user.model';

const USERS_API_URL = `${environment.apiBaseUrl}/api/v1/customers`;

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly http = inject(HttpClient);
  private readonly datePipe = inject(DatePipe);

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
    const requestBody = {
      ...user,
      dateOfBirth: this.datePipe.transform(user.dateOfBirth, 'yyyy-MM-dd') as string,
    };

    return this.http.post<User>(USERS_API_URL, requestBody, this.httpOptions);
  }

  updateUser(id: string, user: { firstName: string; lastName: string; dateOfBirth: Date }): Observable<void> {
    const requestBody = {
      ...user,
      dateOfBirth: this.datePipe.transform(user.dateOfBirth, 'yyyy-MM-dd') as string,
    };

    return this.http.put<void>(`${USERS_API_URL}/${id}`, requestBody, this.httpOptions);
  }

  updateUserContact(id: string, contact: { email: string; phone: string }): Observable<void> {
    return this.http.put<void>(`${USERS_API_URL}/${id}/contact`, contact, this.httpOptions);
  }

  updateUserAddress(id: string, address: UserAddress): Observable<void> {
    return this.http.put<void>(`${USERS_API_URL}/${id}/address`, address, this.httpOptions);
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${USERS_API_URL}/${id}`);
  }
}
