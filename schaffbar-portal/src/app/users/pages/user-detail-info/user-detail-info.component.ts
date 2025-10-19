import { Component, inject, input } from '@angular/core';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

import { UserAddressComponent } from '../../components/user-address/user-address.component';
import { UserContactComponent } from '../../components/user-contact/user-contact.component';
import { User } from '../../user.model';

@Component({
  selector: 'schbar-user-detail-info',
  templateUrl: './user-detail-info.component.html',
  styleUrl: './user-detail-info.component.scss',
  imports: [UserAddressComponent, UserContactComponent, MatSlideToggleModule, MatIconModule, MatDialogModule],
})
export class UserDetailInfoComponent {
  user = input.required<User>();

  private dialog = inject(MatDialog);

  openEditAddressDialog() {
    // this.dialog.open(EditAddressDialogComponent, {
    //   data: { ...this.user().address },
    //   width: '400px',
    //   disableClose: true,
    // });
  }
}
