import { LowerCasePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { ChangeRfidReaderTypeCommand, RfidReader, RfidReaderType } from '../../rfid-reader.model';

export interface ChangeRfidReaderTypeDialogData {
  rfidReader: RfidReader;
}

@Component({
  selector: 'schbar-change-rfid-reader-type-form',
  templateUrl: './change-rfid-reader-type-form.component.html',
  styleUrl: './change-rfid-reader-type-form.component.scss',
  imports: [
    LowerCasePipe,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    TranslatePipe,
  ],
})
export class ChangeRfidReaderTypeFormComponent {
  protected readonly data: ChangeRfidReaderTypeDialogData = inject(MAT_DIALOG_DATA);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ChangeRfidReaderTypeFormComponent>);

  protected readonly rfidReaderTypes = Object.values(RfidReaderType);

  protected typeForm = this.fb.group({
    type: [this.data.rfidReader.type, Validators.required],
  });

  protected save(): void {
    if (this.typeForm.invalid) {
      this.typeForm.markAllAsTouched();
      return;
    }

    const command: ChangeRfidReaderTypeCommand = {
      id: this.data.rfidReader.id,
      type: this.typeForm.controls.type.value,
    };

    this.dialogRef.close(command);
  }
}
