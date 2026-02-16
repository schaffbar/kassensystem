import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReader } from '../../../rfid-readers/rfid-reader.model';
import { Tool } from '../../tool.model';

export interface UpdateToolCommand {
  id: string;
  name: string;
  description?: string;
}

@Component({
  selector: 'schbar-tool-detail-info',
  templateUrl: './tool-detail-info.component.html',
  styleUrl: './tool-detail-info.component.scss',
  imports: [MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, ReactiveFormsModule, TranslatePipe],
})
export class ToolDetailInfoComponent {
  tool = input.required<Tool>();
  rfidReaders = input.required<RfidReader[]>();

  toolChanged = output<UpdateToolCommand>();

  private fb = inject(NonNullableFormBuilder);

  protected readonly = signal(true);
  protected assignedRfidReader = computed(() => {
    const readerId = this.tool().rfidReaderId;
    return this.rfidReaders().find((reader) => reader.id === readerId);
  });

  toolForm = this.fb.group({
    name: [''],
    description: [''],
    rfidReaderId: [''],
  });

  initialValues = effect(() => this.setInitialFormValues());

  updateTool() {
    this.readonly.set(false);
  }

  onCancel() {
    this.setInitialFormValues();
    this.readonly.set(true);
  }

  onSave() {
    if (this.toolForm.valid) {
      const formValues = this.toolForm.value;
      const command: UpdateToolCommand = {
        id: this.tool().id,
        name: formValues.name!,
        description: formValues.description || undefined,
      };

      this.toolChanged.emit(command);
      this.readonly.set(true);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private setInitialFormValues() {
    const tool = this.tool();
    this.toolForm.setValue({
      name: tool.name,
      description: tool.description || '',
      rfidReaderId: tool.rfidReaderId || '',
    });
  }
}
