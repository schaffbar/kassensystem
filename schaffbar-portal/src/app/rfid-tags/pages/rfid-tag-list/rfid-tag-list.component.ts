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

import { ConfirmDeleteComponent } from '../../../shared/components/confirm-delete/confirm-delete.component';
import { RfidTag } from '../../rfid-tag.model';
import { RfidTagStore } from '../../rfid-tags.store';

@Component({
  selector: 'schbar-rfid-tag-list',
  templateUrl: './rfid-tag-list.component.html',
  styleUrl: './rfid-tag-list.component.scss',
  imports: [
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    TranslatePipe,
  ],
  providers: [RfidTagStore],
})
export class RfidTagListComponent {
  readonly store = inject(RfidTagStore);
  readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly translate = inject(TranslateService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected displayedColumns = ['id', 'active', 'actions'];
  protected rfidTagsCount = computed(() => this.store.entities().length);
  protected dataSource = computed(() => {
    const result = new MatTableDataSource<RfidTag>(this.store.entities());
    result.sort = this.sort;
    result.paginator = this.paginator;
    return result;
  });

  protected reloadRfidTags(): void {
    this.store.loadAllRfidTags();
  }

  protected deleteRfidTagDialog(event: Event, rfidTag: RfidTag): void {
    event.stopPropagation();

    if (!rfidTag || !rfidTag.id) {
      console.warn('No RFID tag selected for deletion');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('rfidTags.dialogs.deleteRfidTag.title'),
        entity: rfidTag.id,
        message: this.translate.instant('rfidTags.dialogs.deleteRfidTag.message'),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.store.deleteRfidTag(rfidTag.id);
      }
    });
  }
}
