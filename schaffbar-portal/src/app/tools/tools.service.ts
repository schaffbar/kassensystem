import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Tool } from './tool.model';

const TOOLS_API_URL = 'http://localhost:5000/api/v1/tools';

@Injectable({ providedIn: 'root' })
export class ToolsService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  getTools(): Observable<Tool[]> {
    return this.http.get<Tool[]>(TOOLS_API_URL);
  }

  getTool(id: string): Observable<Tool> {
    return this.http.get<Tool>(`${TOOLS_API_URL}/${id}`);
  }

  createTool(tool: Tool): Observable<Tool> {
    return this.http.post<Tool>(TOOLS_API_URL, tool, this.httpOptions);
  }

  deleteTool(id: string): Observable<void> {
    return this.http.delete<void>(`${TOOLS_API_URL}/${id}`);
  }
}
