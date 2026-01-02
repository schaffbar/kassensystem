import { Component, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { UserAddress } from '../../user.model';

@Component({
  selector: 'schbar-user-address',
  templateUrl: './user-address.component.html',
  styleUrl: './user-address.component.scss',
  imports: [
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatRadioModule,
    MatCardModule,
    MatIconModule,
    ReactiveFormsModule,
    TranslatePipe,
  ],
})
export class UserAddressComponent {
  userAddress = input.required<UserAddress>();

  addressChanged = output<UserAddress>();

  private fb = inject(NonNullableFormBuilder);

  readonly = signal(true);

  userAddressForm = this.fb.group({
    addressLine1: ['', Validators.required],
    addressLine2: [''],
    postalCode: ['', Validators.compose([Validators.required, Validators.minLength(5), Validators.maxLength(5)])],
    city: ['', Validators.required],
    country: ['', Validators.required],
  });

  initialValues = effect(() => this.setInitialFormValues());

  updateAddress() {
    this.readonly.set(false);
  }

  onCancel() {
    this.setInitialFormValues();
    this.readonly.set(true);
  }

  onSave() {
    if (this.userAddressForm.valid) {
      const formValues = this.userAddressForm.value;
      // TODO: check if address really changed
      // TODO: try to solve it in another way
      const newAddress: UserAddress = {
        addressLine1: formValues.addressLine1 ?? '',
        addressLine2: formValues.addressLine2 ?? '',
        postalCode: formValues.postalCode ?? '',
        city: formValues.city ?? '',
        country: formValues.country ?? '',
      };

      this.addressChanged.emit(newAddress);
      this.readonly.set(true);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private setInitialFormValues() {
    const address = this.userAddress();
    this.userAddressForm.setValue({
      ...address,
      addressLine2: address.addressLine2 || '',
    });
  }
}
