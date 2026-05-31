import { DatePipe } from '@angular/common';
import { Component, ElementRef, computed, inject, input, output, signal, viewChild } from '@angular/core';

import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';

import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { ConfirmDeleteComponent } from '../../../shared/components/confirm-delete/confirm-delete.component';
import {
  BatchCreateToolCertificationCommand,
  ToolCertification,
  ToolCertificationStatus,
} from '../../../users/shared/models/tool-certification.model';
import { User } from '../../../users/shared/models/user.model';

@Component({
  selector: 'schbar-tool-certifications',
  templateUrl: './tool-certifications.component.html',
  styleUrl: './tool-certifications.component.scss',
  imports: [
    DatePipe,
    MatAutocompleteModule,
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatTableModule,
    TranslatePipe,
  ],
})
export class ToolCertificationsComponent {
  private readonly dialog = inject(MatDialog);
  private readonly translate = inject(TranslateService);

  toolId = input.required<string>();
  certifications = input.required<ToolCertification[]>();
  allUsers = input.required<User[]>();
  instructorIds = input.required<string[]>();

  batchCreate = output<BatchCreateToolCertificationCommand>();
  deleteCertification = output<{ customerId: string; toolId: string }>();

  readonly displayedColumns: string[] = ['userName', 'status', 'certifiedAt', 'actions'];
  readonly CertificationStatus = ToolCertificationStatus;

  searchText = signal('');
  selectedUsers = signal<User[]>([]);
  selectedCertifier = signal<User | null>(null);
  certifierSearchText = signal('');

  private userSearchInput = viewChild<ElementRef>('userSearchInput');
  private certifierSearchInput = viewChild<ElementRef>('certifierSearchInput');

  userNameMap = computed(() => {
    const map = new Map<string, string>();
    for (const u of this.allUsers()) {
      map.set(u.id, `${u.firstName} ${u.lastName}`);
    }
    return map;
  });

  filteredUsers = () => {
    const search = this.searchText().toLowerCase();
    const selectedIds = new Set(this.selectedUsers().map((u) => u.id));
    const certifiedUserIds = new Set(this.certifications().map((c) => c.customerId));
    const instructorIdSet = new Set(this.instructorIds());
    return this.allUsers().filter(
      (u) =>
        !selectedIds.has(u.id) &&
        !certifiedUserIds.has(u.id) &&
        !instructorIdSet.has(u.id) &&
        (u.firstName + ' ' + u.lastName).toLowerCase().includes(search),
    );
  };

  filteredCertifiers = () => {
    const search = this.certifierSearchText().toLowerCase();
    const ids = new Set(this.instructorIds());
    return this.allUsers().filter(
      (u) => ids.has(u.id) && (u.firstName + ' ' + u.lastName).toLowerCase().includes(search),
    );
  };

  onUserSelected(user: User): void {
    this.selectedUsers.update((users) => [...users, user]);
    this.searchText.set('');
    this.userSearchInput()?.nativeElement.blur();
  }

  removeSelectedUser(user: User): void {
    this.selectedUsers.update((users) => users.filter((u) => u.id !== user.id));
  }

  onCertifierInput(value: string): void {
    this.certifierSearchText.set(value);
    if (this.selectedCertifier()) {
      this.selectedCertifier.set(null);
    }
  }

  onCertifierSelected(user: User): void {
    this.selectedCertifier.set(user);
    this.certifierSearchText.set(`${user.firstName} ${user.lastName}`);
    this.certifierSearchInput()?.nativeElement.blur();
  }

  onSubmitBatch(): void {
    const certifier = this.selectedCertifier();
    if (this.selectedUsers().length === 0 || !certifier) return;

    this.batchCreate.emit({
      customerIds: this.selectedUsers().map((u) => u.id),
      toolId: this.toolId(),
      certifiedBy: certifier.id,
    });

    this.selectedUsers.set([]);
    this.selectedCertifier.set(null);
    this.certifierSearchText.set('');
  }

  onDelete(cert: ToolCertification): void {
    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('users.dialogs.certifications.deleteCertification.title'),
        message: this.translate.instant('users.dialogs.certifications.deleteCertification.message'),
        entity: this.userNameMap().get(cert.customerId),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteCertification.emit({ customerId: cert.customerId, toolId: cert.toolId });
      }
    });
  }
}
