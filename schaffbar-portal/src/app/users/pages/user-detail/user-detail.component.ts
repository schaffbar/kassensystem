import { LowerCasePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { TranslatePipe } from '@ngx-translate/core';

import { UpdateUserFormComponent } from '../../components/update-user-form/update-user-form.component';
import { RfidTagAssignmentService } from '../../rfid-tag-assignment.service';
import { User, UserAddress } from '../../user.model';
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
    TranslatePipe,
    LowerCasePipe,
  ],
  providers: [UserDetailStore, RfidTagAssignmentService],
})
export class UserDetailComponent {
  private readonly dialog = inject(MatDialog);
  protected readonly detailsStore = inject(UserDetailStore);

  id = input.required<string>();

  constructor() {
    this.detailsStore.setUserId(this.id);
  }

  protected reloadUser(): void {
    this.detailsStore.reloadUser();
  }

  protected updateUser(): void {
    const dialogRef = this.dialog.open(UpdateUserFormComponent, {
      data: {
        ...this.detailsStore.user(),
      },
      minWidth: '800px',
      disableClose: true,
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((user: User) => {
      if (user) {
        this.detailsStore.updateUser({
          id: this.id(),
          firstName: user.firstName,
          lastName: user.lastName,
          dateOfBirth: user.dateOfBirth,
          clubMember: user.clubMember,
        });
      }
    });
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
    this.detailsStore.updateUserAddress({ id: this.id(), address });
  }

  protected onContactChanged(contact: { email: string; phone: string }) {
    this.detailsStore.updateUserContact({ id: this.id(), ...contact });
  }
}
