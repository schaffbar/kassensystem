import { Component, computed, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReaderType } from '../../../rfid-readers/rfid-reader.model';
import { RfidReaderStore } from '../../../rfid-readers/rfid-readers.store';
import { CreateToolCommand } from '../../tool.model';

@Component({
  selector: 'schbar-new-tool-form',
  templateUrl: './new-tool-form.component.html',
  styleUrl: './new-tool-form.component.scss',
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    MatFormFieldModule,
    MatSelectModule,
    TranslatePipe,
  ],
})
export class NewToolFormComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly dialogRef = inject(MatDialogRef<NewToolFormComponent>);
  private readonly rfidReaderStore = inject(RfidReaderStore);

  protected toolForm = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    rfidReaderId: [''],
  });

  protected rfidReaders = computed(() =>
    this.rfidReaderStore.entities().filter((reader) => reader.type === RfidReaderType.SwitchBox),
  );

  constructor() {
    this.rfidReaderStore.loadAllRfidReaders();
  }

  protected save(): void {
    if (this.toolForm.invalid) {
      this.toolForm.markAllAsTouched();
      return;
    }

    const formValue = this.toolForm.value;
    console.log('Form Value:', formValue.rfidReaderId || undefined); // Debug log

    const command: CreateToolCommand = {
      name: formValue.name!,
      description: formValue.description || undefined,
      rfidReaderId: formValue.rfidReaderId || undefined,
    };

    this.dialogRef.close(command);
  }
}
