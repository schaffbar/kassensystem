import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'schbar-new-user-form',
  templateUrl: './new-user-form.component.html',
  styleUrl: './new-user-form.component.scss',
  imports: [ReactiveFormsModule, MatInputModule, MatButtonModule, MatDialogModule, MatDividerModule],
})
export class NewUserFormComponent {
  private fb = inject(NonNullableFormBuilder);
  private dialogRef = inject(MatDialogRef<NewUserFormComponent>);

  protected userForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', Validators.required],
    phone: ['', Validators.required],
    addressLine1: ['', Validators.required],
    addressLine2: [''],
    postalCode: ['', Validators.required],
    city: ['', Validators.required],
    country: ['', Validators.required],
  });

  protected save() {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.dialogRef.close(this.userForm.value);
  }
}
