import { LowerCasePipe } from '@angular/common';
import { Component, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReader, RfidReaderType, UpdateRfidReaderCommand } from '../../rfid-reader.model';

@Component({
  selector: 'schbar-rfid-reader-detail-info',
  templateUrl: './rfid-reader-detail-info.component.html',
  styleUrl: './rfid-reader-detail-info.component.scss',
  imports: [
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    TranslatePipe,
    LowerCasePipe,
  ],
})
export class RfidReaderDetailInfoComponent {
  rfidReader = input.required<RfidReader>();

  readerChanged = output<UpdateRfidReaderCommand>();

  private fb = inject(NonNullableFormBuilder);

  readonly = signal(true);

  protected rfidReaderTypes = Object.values(RfidReaderType);

  rfidReaderForm = this.fb.group({
    macAddress: [{ value: '', disabled: true }],
    type: ['', Validators.required],
    name: [''],
    socketName: [''],
  });

  initialValues = effect(() => this.setInitialFormValues());

  updateReader() {
    this.readonly.set(false);
  }

  onCancel() {
    this.setInitialFormValues();
    this.readonly.set(true);
  }

  onSave() {
    if (this.rfidReaderForm.valid) {
      const formValues = this.rfidReaderForm.value;
      const command: UpdateRfidReaderCommand = {
        id: this.rfidReader().id,
        type: formValues.type as RfidReaderType,
        name: formValues.name || undefined,
        socketName: formValues.socketName || undefined,
      };

      this.readerChanged.emit(command);
      this.readonly.set(true);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private setInitialFormValues() {
    const reader = this.rfidReader();
    this.rfidReaderForm.setValue({
      macAddress: reader.macAddress,
      type: reader.type,
      name: reader.name || '',
      socketName: reader.socketName || '',
    });
  }
}
