import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { RfidTagAssignmentService } from '../../rfid-tag-assignment.service';
import { UserAddress } from '../../user.model';
import { UserDetailInfoComponent } from '../user-detail-info/user-detail-info.component';
import { UserOpenSessionComponent } from '../user-open-session/user-open-session.component';
import { UserDetailStore } from './user-detail.store';

@Component({
  selector: 'schbar-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrl: './user-detail.component.scss',
  imports: [
    UserDetailInfoComponent,
    UserOpenSessionComponent,
    MatTabsModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
  ],
  providers: [UserDetailStore, RfidTagAssignmentService],
})
export class UserDetailComponent {
  protected readonly detailsStore = inject(UserDetailStore);

  id = input.required<string>();

  constructor() {
    this.detailsStore.setUserId(this.id);
  }

  protected reloadUser(): void {
    this.detailsStore.reloadUser();
  }

  protected updateUser(): void {
    console.log('Update user clicked');
  }

  protected assignFixedRfidTag(): void {
    this.detailsStore.assignFixedRfidTag(this.id);
  }

  protected assignTemporaryRfidTag(): void {
    this.detailsStore.assignTemporaryRfidTag(this.id);
  }

  protected unassignRfidTag(): void {
    this.detailsStore.unassignRfidTag(this.id);
  }

  protected onAddressChanged(address: UserAddress) {
    console.log('Address changed:', address);
    this.detailsStore.updateUserAddress({ id: this.id(), address });
  }

  protected onContactChanged(contact: { email: string; phone: string }) {
    console.log('Contact changed:', contact);
    this.detailsStore.updateUserContact({ id: this.id(), ...contact });
  }
}
