import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';

import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { RfidReader, RfidReaderType } from '../../../rfid-readers/rfid-reader.model';
import { ConfirmDeleteComponent } from '../../../shared/components/confirm-delete/confirm-delete.component';
import { Tool } from '../../tool.model';

export interface ChangeRfidReaderCommand {
  toolId: string;
  rfidReaderId?: string;
}

export interface ClearRfidReaderCommand {
  toolId: string;
}

@Component({
  selector: 'schbar-tool-rfid-reader-assignment',
  templateUrl: './tool-rfid-reader-assignment.component.html',
  styleUrl: './tool-rfid-reader-assignment.component.scss',
  imports: [
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    FormsModule,
    TranslatePipe,
  ],
})
export class ToolRfidReaderAssignmentComponent {
  private readonly dialog = inject(MatDialog);
  private readonly translate = inject(TranslateService);

  tool = input.required<Tool>();
  rfidReaders = input.required<RfidReader[]>();
  assignedRfidReaderIds = input<Set<string>>(new Set());

  rfidReaderChanged = output<ChangeRfidReaderCommand>();
  rfidReaderCleared = output<ClearRfidReaderCommand>();

  protected selectedRfidReaderId = signal('');

  protected isDirty = computed(() => {
    const current = this.tool().rfidReaderId || '';
    return this.selectedRfidReaderId() !== current;
  });

  protected isSelectedAssigned = computed(() => {
    const selectedId = this.selectedRfidReaderId();
    return !!selectedId && this.assignedRfidReaderIds().has(selectedId);
  });

  protected filteredRfidReaders = computed(() => {
    return this.rfidReaders().filter((reader) => reader.type === RfidReaderType.SwitchBox);
  });

  protected isAssigned(readerId: string): boolean {
    return this.assignedRfidReaderIds().has(readerId);
  }

  syncSelection = effect(() => {
    this.selectedRfidReaderId.set(this.tool().rfidReaderId || '');
  });

  onCancel() {
    this.selectedRfidReaderId.set(this.tool().rfidReaderId || '');
  }

  onSave() {
    const command: ChangeRfidReaderCommand = {
      toolId: this.tool().id,
      rfidReaderId: this.selectedRfidReaderId() || undefined,
    };

    this.rfidReaderChanged.emit(command);
  }

  onClear() {
    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('tools.dialogs.clearRfidReader.title'),
        message: this.translate.instant('tools.dialogs.clearRfidReader.message'),
        entity: this.tool().name,
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.rfidReaderCleared.emit({
          toolId: this.tool().id,
        });
      }
    });
  }
}
