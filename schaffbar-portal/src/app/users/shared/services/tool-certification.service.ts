import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { BatchCreateToolCertificationCommand, ToolCertification } from '../models/tool-certification.model';

const TOOL_CERTIFICATIONS_API_URL = `${environment.apiBaseUrl}/api/v1/tool-certifications`;

@Injectable({ providedIn: 'root' })
export class ToolCertificationService {
  private readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  // --------------------------------------------------------------------------
  // queries

  getCertificationsByUser(customerId: string): Observable<ToolCertification[]> {
    return this.http.get<ToolCertification[]>(`${TOOL_CERTIFICATIONS_API_URL}?customerId=${customerId}`);
  }

  getCertificationsByTool(toolId: string): Observable<ToolCertification[]> {
    return this.http.get<ToolCertification[]>(`${TOOL_CERTIFICATIONS_API_URL}?toolId=${toolId}`);
  }

  // --------------------------------------------------------------------------
  // commands

  batchCreate(command: BatchCreateToolCertificationCommand): Observable<void> {
    return this.http.post<void>(`${TOOL_CERTIFICATIONS_API_URL}/batch`, command, this.httpOptions);
  }

  pause(customerId: string, toolId: string): Observable<void> {
    return this.http.put<void>(
      `${TOOL_CERTIFICATIONS_API_URL}/customers/${customerId}/tools/${toolId}/pause`,
      {},
      this.httpOptions,
    );
  }

  reactivate(customerId: string, toolId: string): Observable<void> {
    return this.http.put<void>(
      `${TOOL_CERTIFICATIONS_API_URL}/customers/${customerId}/tools/${toolId}/reactivate`,
      {},
      this.httpOptions,
    );
  }

  revoke(customerId: string, toolId: string): Observable<void> {
    return this.http.put<void>(
      `${TOOL_CERTIFICATIONS_API_URL}/customers/${customerId}/tools/${toolId}/revoke`,
      {},
      this.httpOptions,
    );
  }

  delete(customerId: string, toolId: string): Observable<void> {
    return this.http.delete<void>(`${TOOL_CERTIFICATIONS_API_URL}/customers/${customerId}/tools/${toolId}`);
  }
}
