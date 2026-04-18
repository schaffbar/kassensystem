import { LowerCasePipe } from '@angular/common';
import { Component, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReader, UpdateRfidReaderCommand } from '../../rfid-reader.model';

@Component({
  selector: 'schbar-rfid-reader-detail-info',
  templateUrl: './rfid-reader-detail-info.component.html',
  styleUrl: './rfid-reader-detail-info.component.scss',
  imports: [MatInputModule, MatButtonModule, MatIconModule, ReactiveFormsModule, TranslatePipe, LowerCasePipe],
})
export class RfidReaderDetailInfoComponent {
  rfidReader = input.required<RfidReader>();

  readerChanged = output<UpdateRfidReaderCommand>();

  private fb = inject(NonNullableFormBuilder);

  protected readonly = signal(true);

  // TODO: make type read only initially
  rfidReaderForm = this.fb.group({
    macAddress: ['', Validators.required],
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
      name: reader.name || '',
      socketName: reader.socketName || '',
    });
  }
}
