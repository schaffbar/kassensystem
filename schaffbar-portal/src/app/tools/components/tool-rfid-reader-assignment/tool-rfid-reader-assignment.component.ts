import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReader } from '../../../rfid-readers/rfid-reader.model';
import { Tool } from '../../tool.model';

export interface ChangeRfidReaderCommand {
  toolId: string;
  rfidReaderId?: string;
}

@Component({
  selector: 'schbar-tool-rfid-reader-assignment',
  templateUrl: './tool-rfid-reader-assignment.component.html',
  styleUrl: './tool-rfid-reader-assignment.component.scss',
  imports: [MatSelectModule, MatButtonModule, MatIconModule, ReactiveFormsModule, TranslatePipe],
})
export class ToolRfidReaderAssignmentComponent {
  private fb = inject(NonNullableFormBuilder);

  tool = input.required<Tool>();
  rfidReaders = input.required<RfidReader[]>();

  rfidReaderChanged = output<ChangeRfidReaderCommand>();

  protected readonly = signal(true);
  protected assignedRfidReader = computed(() => {
    const readerId = this.tool().rfidReaderId;
    return this.rfidReaders().find((reader) => reader.id === readerId);
  });

  rfidReaderForm = this.fb.group({
    rfidReaderId: [''],
  });

  initialValues = effect(() => this.setInitialFormValues());

  updateRfidReader() {
    this.readonly.set(false);
  }

  onCancel() {
    this.setInitialFormValues();
    this.readonly.set(true);
  }

  onSave() {
    if (this.rfidReaderForm.valid) {
      const formValues = this.rfidReaderForm.value;
      const command: ChangeRfidReaderCommand = {
        toolId: this.tool().id,
        rfidReaderId: formValues.rfidReaderId || undefined,
      };

      this.rfidReaderChanged.emit(command);
      this.readonly.set(true);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private setInitialFormValues() {
    const tool = this.tool();
    this.rfidReaderForm.setValue({
      rfidReaderId: tool.rfidReaderId || '',
    });
  }
}
