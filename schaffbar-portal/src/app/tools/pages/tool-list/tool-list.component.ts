import { LowerCasePipe } from '@angular/common';
import { Component, computed, inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { ROUTE } from '../../../app.routes';
import { ConfirmDeleteComponent } from '../../../shared/components/confirm-delete/confirm-delete.component';
import { NewToolFormComponent } from '../../components/new-tool-form/new-tool-form.component';
import { CreateToolCommand, Tool } from '../../tool.model';
import { ToolsStore } from '../../tools.store';

@Component({
  selector: 'schbar-tool-list',
  templateUrl: './tool-list.component.html',
  styleUrl: './tool-list.component.scss',
  imports: [
    LowerCasePipe,
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    TranslatePipe,
  ],
  providers: [ToolsStore],
})
export class ToolListComponent {
  readonly store = inject(ToolsStore);
  readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly translate = inject(TranslateService);

  // TODO: replace with signal version
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected displayedColumns = [
    'certificationRequirement',
    'name',
    'area',
    'description',
    'rfidReader',
    'wlanRelaisType',
    'actions',
  ];
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

  protected reloadTools(): void {
    this.store.loadAllTools();
  }

  protected newToolDialog(): void {
    const dialogRef = this.dialog.open(NewToolFormComponent, {
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result: CreateToolCommand | null) => {
      if (result) {
        this.store.createTool(result);
      }
    });
  }

  protected deleteToolDialog(event: Event, tool: Tool): void {
    event.stopPropagation();

    if (!tool || !tool.id) {
      console.warn('No tool selected for deletion');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('tools.dialogs.deleteTool.title'),
        entity: tool.name,
        message: this.translate.instant('tools.dialogs.deleteTool.message'),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.store.deleteTool(tool.id);
      }
    });
  }
}
