import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { UpdateToolCommand } from './components/tool-detail-info/tool-detail-info.component';
import { ChangeRfidReaderCommand } from './components/tool-rfid-reader-assignment/tool-rfid-reader-assignment.component';
import { Tool } from './tool.model';

const TOOLS_API_URL = `${environment.apiBaseUrl}/api/v1/tools`;

@Injectable({ providedIn: 'root' })
export class ToolsService {
  readonly http = inject(HttpClient);

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json', //
      // Authorization: 'Bearer my-bearer', //
    }),
  };

  // --------------------------------------------------------------------------
  // queries

  getTools(): Observable<Tool[]> {
    return this.http.get<Tool[]>(TOOLS_API_URL);
  }

  getTool(id: string): Observable<Tool> {
    return this.http.get<Tool>(`${TOOLS_API_URL}/${id}`);
  }

  // --------------------------------------------------------------------------
  // commands

  createTool(tool: Tool): Observable<Tool> {
    return this.http.post<Tool>(TOOLS_API_URL, tool, this.httpOptions);
  }

  updateTool(command: UpdateToolCommand): Observable<void> {
    return this.http.put<void>(`${TOOLS_API_URL}/${command.id}`, command, this.httpOptions);
  }

  changeRfidReader(command: ChangeRfidReaderCommand): Observable<void> {
    return this.http.put<void>(
      `${TOOLS_API_URL}/${command.toolId}/rfid-reader/${command.rfidReaderId}`,
      command,
      this.httpOptions,
    );
  }

  deleteTool(id: string): Observable<void> {
    return this.http.delete<void>(`${TOOLS_API_URL}/${id}`);
  }
}
