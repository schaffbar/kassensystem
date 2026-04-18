import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';

import { TranslateService } from '@ngx-translate/core';
import { take } from 'rxjs';

import { ToolsService } from '../../../tools/tools.service';
import { ChangeRfidReaderTypeFormComponent } from '../../components/change-rfid-reader-type-form/change-rfid-reader-type-form.component';
import { RfidReaderDetailInfoComponent } from '../../components/rfid-reader-detail-info/rfid-reader-detail-info.component';
import {
  ChangeRfidReaderTypeCommand,
  RfidReader,
  RfidReaderType,
  UpdateRfidReaderCommand,
} from '../../rfid-reader.model';
import { RfidReaderDetailStore } from './rfid-reader-detail.store';

@Component({
  selector: 'schbar-rfid-reader-detail',
  templateUrl: './rfid-reader-detail.component.html',
  styleUrl: './rfid-reader-detail.component.scss',
  imports: [RfidReaderDetailInfoComponent, MatTabsModule, MatButtonModule, MatChipsModule, MatIconModule],
  providers: [RfidReaderDetailStore],
})
export class RfidReaderDetailComponent {
  protected readonly detailsStore = inject(RfidReaderDetailStore);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly toolsService = inject(ToolsService);
  private readonly translate = inject(TranslateService);

  id = input.required<string>();

  constructor() {
    this.detailsStore.setRfidReaderId(this.id);
  }

  protected reloadRfidReader(): void {
    this.detailsStore.reloadRfidReader();
  }

  // TODO: rename to onRfidReaderChanged
  protected onReaderChanged(command: UpdateRfidReaderCommand): void {
    this.detailsStore.updateRfidReader(command);
  }

  protected changeType(): void {
    const reader = this.detailsStore.rfidReader();
    if (!reader) return;

    if (reader.type === RfidReaderType.SwitchBox) {
      this.toolsService
        .getToolsByRfidReader(reader.id)
        .pipe(take(1))
        .subscribe((tools) => {
          if (tools.length > 0) {
            this.snackBar.open(
              this.translate.instant('rfidReaders.dialogs.changeRfidReaderType.errors.switchBoxHasAssignedTool'),
              this.translate.instant('shared.dialogs.actions.delete.cancel'),
              { duration: 6000 },
            );
          } else {
            this.openChangeTypeDialog(reader);
          }
        });
    } else {
      this.openChangeTypeDialog(reader);
    }
  }

  private openChangeTypeDialog(rfidReader: RfidReader): void {
    const dialogRef = this.dialog.open(ChangeRfidReaderTypeFormComponent, {
      minWidth: '400px',
      disableClose: true,
      data: { rfidReader },
    });

    dialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((result: ChangeRfidReaderTypeCommand | false) => {
        if (result) {
          this.detailsStore.changeRfidReaderType(result);
        }
      });
  }
}
