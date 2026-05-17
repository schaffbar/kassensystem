import { LowerCasePipe } from '@angular/common';
import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { TranslatePipe } from '@ngx-translate/core';

import { ToolsStore } from '../../../tools/tools.store';
import { UpdateUserFormComponent } from '../../components/update-user-form/update-user-form.component';
import { UserToolCertificationsComponent } from '../../components/user-tool-certifications/user-tool-certifications.component';
import { User, UserAddress } from '../../shared/models/user.model';
import { RfidTagAssignmentService } from '../../shared/services/rfid-tag-assignment.service';
import { UserDetailStore } from '../../shared/stores/user-detail.store';
import { UserDetailInfoComponent } from '../user-detail-info/user-detail-info.component';
import { UserOpenSessionComponent } from '../user-open-session/user-open-session.component';

@Component({
  selector: 'schbar-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrl: './user-detail.component.scss',
  imports: [
    UserDetailInfoComponent,
    UserOpenSessionComponent,
    UserToolCertificationsComponent,
    MatTabsModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    TranslatePipe,
    LowerCasePipe,
  ],
  providers: [UserDetailStore, RfidTagAssignmentService],
})
export class UserDetailComponent implements OnInit {
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);
  protected readonly detailsStore = inject(UserDetailStore);
  private readonly toolsStore = inject(ToolsStore);

  id = input.required<string>();

  protected selectedTabIndex = signal(0);
  protected allTools = computed(() => this.toolsStore.entities());

  constructor() {
    this.detailsStore.setUserId(this.id);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab'] === 'open-session') {
        this.selectedTabIndex.set(1);
      }
    });
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

  protected onPauseCertification(event: { customerId: string; toolId: string }): void {
    this.detailsStore.pauseCertification(event);
  }

  protected onReactivateCertification(event: { customerId: string; toolId: string }): void {
    this.detailsStore.reactivateCertification(event);
  }

  protected onRevokeCertification(event: { customerId: string; toolId: string }): void {
    this.detailsStore.revokeCertification(event);
  }
}
