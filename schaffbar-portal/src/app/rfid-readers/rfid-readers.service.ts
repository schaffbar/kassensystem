import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { RfidReader, UpdateRfidReaderCommand } from './rfid-reader.model';

const RFID_READERS_API_URL = `${environment.apiBaseUrl}/api/v1/rfid-readers`;

@Injectable({ providedIn: 'root' })
export class RfidReaderService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  // --------------------------------------------------------------------------
  // queries

  getRfidReaders(): Observable<RfidReader[]> {
    return this.http.get<RfidReader[]>(RFID_READERS_API_URL);
  }

  getRfidReader(id: string): Observable<RfidReader> {
    return this.http.get<RfidReader>(`${RFID_READERS_API_URL}/${id}`);
  }

  // --------------------------------------------------------------------------
  // commands

  updateRfidReader(command: UpdateRfidReaderCommand): Observable<void> {
    return this.http.put<void>(`${RFID_READERS_API_URL}/${command.id}`, command, this.httpOptions);
  }

  deleteRfidReader(id: string): Observable<void> {
    return this.http.delete<void>(`${RFID_READERS_API_URL}/${id}`);
  }
}
