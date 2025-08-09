import { Component, computed, inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { RfidTag } from '../../rfid-tag.model';
import { RfidTagStore } from '../../rfid-tag.store';

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
  ],
  providers: [RfidTagStore],
})
export class RfidTagListComponent {
  readonly store = inject(RfidTagStore);
  readonly router = inject(Router);

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

  protected deleteRfidTagDialog(event: Event, rfidTag: RfidTag): void {
    event.stopPropagation();

    if (!rfidTag || !rfidTag.id) {
      console.warn('No RFID tag selected for deletion');
      return;
    }

    this.store.deleteRfidTag(rfidTag.id);
  }
}
