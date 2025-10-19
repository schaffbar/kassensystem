import { Component, input, output } from '@angular/core';

import { MatSlideToggleModule } from '@angular/material/slide-toggle';

import { UserAddressComponent } from '../../components/user-address/user-address.component';
import { UserContactComponent } from '../../components/user-contact/user-contact.component';
import { User, UserAddress } from '../../user.model';

@Component({
  selector: 'schbar-user-detail-info',
  templateUrl: './user-detail-info.component.html',
  styleUrl: './user-detail-info.component.scss',
  imports: [UserAddressComponent, UserContactComponent, MatSlideToggleModule],
})
export class UserDetailInfoComponent {
  user = input.required<User>();

  addressChanged = output<UserAddress>();

  onAddressChanged(address: UserAddress) {
    this.addressChanged.emit(address);
  }
}
