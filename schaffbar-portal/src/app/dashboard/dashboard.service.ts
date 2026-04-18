import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { ActiveTool, ActiveUser } from './dashboard.model';

const DASHBOARD_API_URL = `${environment.apiBaseUrl}/api/v1/workshop-dashboard`;
const DASHBOARD_TOOL_API_URL = `${environment.apiBaseUrl}/api/v1/tool-dashboard`;

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  // --------------------------------------------------------------------------
  // queries

  getActiveUsers(): Observable<ActiveUser[]> {
    return this.http.get<ActiveUser[]>(`${DASHBOARD_API_URL}/active-users`);
  }

  getActiveTools(): Observable<ActiveTool[]> {
    return this.http.get<ActiveTool[]>(`${DASHBOARD_TOOL_API_URL}/active-tools`);
  }
}
