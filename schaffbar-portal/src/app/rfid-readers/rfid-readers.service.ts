import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { RfidReader } from './rfid-reader.model';

const RFID_READERS_API_URL = 'http://localhost:5000/api/v1/rfid-readers';

@Injectable({ providedIn: 'root' })
export class RfidReaderService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  getRfidReaders(): Observable<RfidReader[]> {
    return this.http.get<RfidReader[]>(RFID_READERS_API_URL);
  }

  getRfidReader(id: string): Observable<RfidReader> {
    return this.http.get<RfidReader>(`${RFID_READERS_API_URL}/${id}`);
  }

  createRfidReader(rfidReader: RfidReader): Observable<RfidReader> {
    return this.http.post<RfidReader>(RFID_READERS_API_URL, rfidReader, this.httpOptions);
  }

  deleteRfidReader(id: string): Observable<void> {
    return this.http.delete<void>(`${RFID_READERS_API_URL}/${id}`);
  }
}
