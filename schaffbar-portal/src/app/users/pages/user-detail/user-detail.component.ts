import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { UserAddressComponent } from '../../components/user-address/user-address.component';
import { UserContactComponent } from '../../components/user-contact/user-contact.component';
import { TokenService } from '../../token.service';
import { UserDetailStore } from './user-detail.store';

@Component({
  selector: 'schbar-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrl: './user-detail.component.scss',
  standalone: true,
  imports: [UserAddressComponent, UserContactComponent, MatTabsModule, MatButtonModule, MatChipsModule, MatIconModule],
  providers: [UserDetailStore, TokenService],
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

  protected assignToken(): void {
    this.detailsStore.assignToken(this.id);
  }

  protected assignTemporaryToken(): void {
    this.detailsStore.assignTemporaryToken(this.id);
  }
}
