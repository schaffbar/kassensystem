import { Component, computed, inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { ROUTE } from '../../../app.routes';
import { Tool } from '../../tool.model';
import { ToolsStore } from '../../tools.store';

@Component({
  selector: 'schbar-tool-list',
  templateUrl: './tool-list.component.html',
  styleUrl: './tool-list.component.scss',
  imports: [
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
  ],
  providers: [ToolsStore],
})
export class ToolListComponent {
  readonly store = inject(ToolsStore);
  readonly router = inject(Router);

  // TODO: replace with signal version
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected displayedColumns = ['name', 'description', 'actions'];
  protected toolsCount = computed(() => this.store.entities().length);
  protected dataSource = computed(() => {
    const result = new MatTableDataSource<Tool>(this.store.entities());
    result.sort = this.sort;
    result.paginator = this.paginator;
    return result;
  });

  protected toolDetails(tool: Tool): void {
    this.router.navigate([ROUTE.TOOLS, tool.id]);
  }

  protected newToolDialog(): void {
    console.log('Open new tool dialog');
  }

  protected deleteToolDialog(event: Event, tool: Tool): void {
    event.stopPropagation();

    if (!tool || !tool.id) {
      console.warn('No tool selected for deletion');
      return;
    }

    this.store.deleteTool(tool.id);
  }
}
