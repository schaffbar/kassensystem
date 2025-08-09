import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { RfidTag } from './rfid-tag.model';

const RFID_TAGS_API_URL = 'http://localhost:5000/api/v1/rfid-tags';

@Injectable({ providedIn: 'root' })
export class RfidTagService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  getRfidTags(): Observable<RfidTag[]> {
    return this.http.get<RfidTag[]>(RFID_TAGS_API_URL);
  }

  getRfidTag(id: string): Observable<RfidTag> {
    return this.http.get<RfidTag>(`${RFID_TAGS_API_URL}/${id}`);
  }

  createRfidTag(rfidTag: RfidTag): Observable<RfidTag> {
    return this.http.post<RfidTag>(RFID_TAGS_API_URL, rfidTag, this.httpOptions);
  }

  deleteRfidTag(id: string): Observable<void> {
    return this.http.delete<void>(`${RFID_TAGS_API_URL}/${id}`);
  }
}
