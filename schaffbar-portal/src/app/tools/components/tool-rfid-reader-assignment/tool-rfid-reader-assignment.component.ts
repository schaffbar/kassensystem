import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReader, RfidReaderType } from '../../../rfid-readers/rfid-reader.model';
import { Tool } from '../../tool.model';

export interface ChangeRfidReaderCommand {
  toolId: string;
  rfidReaderId?: string;
}

@Component({
  selector: 'schbar-tool-rfid-reader-assignment',
  templateUrl: './tool-rfid-reader-assignment.component.html',
  styleUrl: './tool-rfid-reader-assignment.component.scss',
  imports: [MatSelectModule, MatButtonModule, FormsModule, TranslatePipe],
})
export class ToolRfidReaderAssignmentComponent {
  tool = input.required<Tool>();
  rfidReaders = input.required<RfidReader[]>();

  rfidReaderChanged = output<ChangeRfidReaderCommand>();

  protected selectedRfidReaderId = signal('');

  protected isDirty = computed(() => {
    const current = this.tool().rfidReaderId || '';
    return this.selectedRfidReaderId() !== current;
  });

  protected filteredRfidReaders = computed(() => {
    return this.rfidReaders().filter((reader) => reader.type === RfidReaderType.SwitchBox);
  });

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
}
