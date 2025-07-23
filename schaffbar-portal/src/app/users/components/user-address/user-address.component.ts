import { Component, effect, inject, input } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';

import { UserAddress } from '../../user.model';

@Component({
  selector: 'schbar-user-address',
  templateUrl: './user-address.component.html',
  styleUrl: './user-address.component.scss',
  imports: [MatInputModule, MatButtonModule, MatSelectModule, MatRadioModule, MatCardModule, ReactiveFormsModule],
})
export class UserAddressComponent {
  userAddress = input.required<UserAddress>();

  private fb = inject(NonNullableFormBuilder);

  userAddressForm = this.fb.group({
    addressLine1: ['', Validators.required],
    addressLine2: [''],
    postalCode: ['', Validators.compose([Validators.required, Validators.minLength(5), Validators.maxLength(5)])],
    city: ['', Validators.required],
    country: ['', Validators.required],
  });

  updateAddress = effect(() => {
    const address = this.userAddress();
    this.userAddressForm.setValue({
      ...address,
      addressLine2: address.addressLine2 || '',
    });
  });
}
