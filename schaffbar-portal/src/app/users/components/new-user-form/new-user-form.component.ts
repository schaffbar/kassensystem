import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'schbar-new-user-form',
  templateUrl: './new-user-form.component.html',
  styleUrl: './new-user-form.component.scss',
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    MatDatepickerModule,
  ],
  providers: [provideNativeDateAdapter()],
})
export class NewUserFormComponent {
  private fb = inject(NonNullableFormBuilder);
  private dialogRef = inject(MatDialogRef<NewUserFormComponent>);

  // TODO: add validators for email, phone, postal code, ...
  protected userForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    dateOfBirth: [null, Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: [null],
    addressLine1: ['', Validators.required],
    addressLine2: [null],
    postalCode: ['710', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
    city: ['Böblingen', Validators.required],
    country: ['Deutschland', Validators.required],
  });

  // TODO: replace with a moment.js implementation
  private readonly _currentYear = new Date().getFullYear();
  readonly minDate = new Date(this._currentYear - 100, 0, 1);
  readonly maxDate = new Date(this._currentYear - 1, 11, 31);

  protected save() {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.dialogRef.close(this.userForm.value);
  }
}
