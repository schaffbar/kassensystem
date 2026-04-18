import { Component, computed, input, output } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { TranslatePipe } from '@ngx-translate/core';

import { Tool } from '../../../tools/tool.model';
import { ActiveTool } from '../../dashboard.model';

interface ToolStatusRow {
  id: string;
  name: string;
  isInUse: boolean;
  firstName?: string;
  lastName?: string;
}

@Component({
  selector: 'schbar-active-tools-widget',
  templateUrl: './active-tools-widget.component.html',
  styleUrl: './active-tools-widget.component.scss',
  imports: [MatButtonModule, MatCardModule, MatTableModule, MatIconModule, TranslatePipe],
})
export class ActiveToolsWidgetComponent {
  readonly tools = input.required<Tool[]>();
  readonly activeTools = input.required<ActiveTool[]>();

  readonly reload = output<void>();

  protected displayedColumns = ['status', 'name', 'user'];
  protected activeToolsCount = computed(() => this.activeTools().length);
  protected dataSource = computed(() => {
    const activeToolMap = new Map(this.activeTools().map((at) => [at.toolId, at]));
    const rows: ToolStatusRow[] = this.tools()
      .map((tool) => {
        const activeTool = activeToolMap.get(tool.id);
        return {
          id: tool.id,
          name: tool.name,
          isInUse: !!activeTool,
          firstName: activeTool?.firstName,
          lastName: activeTool?.lastName,
        };
      })
      .sort((a, b) => Number(b.isInUse) - Number(a.isInUse));
    return new MatTableDataSource<ToolStatusRow>(rows);
  });

  protected reloadActiveTools(): void {
    this.reload.emit();
  }
}
