import { Component, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { TranslatePipe } from '@ngx-translate/core';

import { User } from '../../user.model';

@Component({
  selector: 'schbar-user-contact',
  templateUrl: './user-contact.component.html',
  styleUrl: './user-contact.component.scss',
  imports: [MatInputModule, MatButtonModule, MatIconModule, ReactiveFormsModule, TranslatePipe],
})
export class UserContactComponent {
  user = input.required<User>();

  contactChanged = output<{ email: string; phone: string }>();

  private fb = inject(NonNullableFormBuilder);

  readonly = signal(true);

  userContactForm = this.fb.group({
    email: ['', Validators.required],
    phone: [''],
  });

  initialValues = effect(() => this.setInitialFormValues());

  updateContact() {
    this.readonly.set(false);
  }

  onCancel() {
    this.setInitialFormValues();
    this.readonly.set(true);
  }

  onSave() {
    if (this.userContactForm.valid) {
      const formValues = this.userContactForm.value;
      // TODO: check if contact really changed
      // TODO: try to solve it in another way
      const newContact = {
        email: formValues.email ?? '',
        phone: formValues.phone ?? '',
      };

      this.contactChanged.emit(newContact);
      this.readonly.set(true);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private setInitialFormValues() {
    const user = this.user();
    this.userContactForm.setValue({
      email: user.email,
      phone: user.phone,
    });
  }
}
