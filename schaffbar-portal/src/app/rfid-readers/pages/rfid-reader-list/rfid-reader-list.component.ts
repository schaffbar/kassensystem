import { Component, computed, inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { RfidReader } from '../../rfid-reader.model';
import { RfidReaderStore } from '../../rfid-readers.store';

@Component({
  selector: 'schbar-rfid-reader-list',
  templateUrl: './rfid-reader-list.component.html',
  styleUrl: './rfid-reader-list.component.scss',
  imports: [
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
  ],
  providers: [RfidReaderStore],
})
export class RfidReaderListComponent {
  readonly store = inject(RfidReaderStore);
  readonly router = inject(Router);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected displayedColumns = ['id', 'macAddress', 'actions'];
  protected rfidReadersCount = computed(() => this.store.entities().length);
  protected dataSource = computed(() => {
    const result = new MatTableDataSource<RfidReader>(this.store.entities());
    result.sort = this.sort;
    result.paginator = this.paginator;
    return result;
  });

  protected deleteRfidReaderDialog(event: Event, rfidReader: RfidReader): void {
    event.stopPropagation();

    if (!rfidReader || !rfidReader.id) {
      console.warn('No RFID reader selected for deletion');
      return;
    }

    this.store.deleteRfidReader(rfidReader.id);
  }
}
