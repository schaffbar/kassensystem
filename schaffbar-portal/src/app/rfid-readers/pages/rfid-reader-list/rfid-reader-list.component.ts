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

import { ConfirmDeleteComponent } from '../../../shared/components/confirm-delete/confirm-delete.component';
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
    TranslatePipe,
    LowerCasePipe,
  ],
  providers: [RfidReaderStore],
})
export class RfidReaderListComponent {
  readonly store = inject(RfidReaderStore);
  readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly translate = inject(TranslateService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected displayedColumns = ['macAddress', 'name', 'type', 'actions'];
  protected rfidReadersCount = computed(() => this.store.entities().length);
  protected dataSource = computed(() => {
    const result = new MatTableDataSource<RfidReader>(this.store.entities());
    result.sort = this.sort;
    result.paginator = this.paginator;
    return result;
  });

  protected reloadRfidReaders(): void {
    this.store.loadAllRfidReaders();
  }

  protected navigateToDetail(rfidReader: RfidReader): void {
    this.router.navigate(['/rfid-readers', rfidReader.id]);
  }

  protected deleteRfidReaderDialog(event: Event, rfidReader: RfidReader): void {
    event.stopPropagation();

    if (!rfidReader || !rfidReader.id) {
      console.warn('No RFID reader selected for deletion');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('rfidReaders.dialogs.deleteRfidReader.title'),
        entity: rfidReader.name || rfidReader.macAddress,
        message: this.translate.instant('rfidReaders.dialogs.deleteRfidReader.message'),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.store.deleteRfidReader(rfidReader.id);
      }
    });
  }
}
