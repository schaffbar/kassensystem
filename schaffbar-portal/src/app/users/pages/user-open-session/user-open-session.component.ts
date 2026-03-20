import { DatePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';

import { TranslatePipe } from '@ngx-translate/core';

import { UserDetailStore } from '../../shared/stores/user-detail.store';

@Component({
  selector: 'schbar-user-open-session',
  templateUrl: './user-open-session.component.html',
  styleUrl: './user-open-session.component.scss',
  imports: [MatTableModule, MatButtonModule, MatChipsModule, DatePipe, TranslatePipe],
})
export class UserOpenSessionComponent {
  protected readonly detailsStore = inject(UserDetailStore);

  userId = input.required<string>();

  displayedColumns: string[] = ['entryTime', 'exitTime', 'durationInMinutes', 'unitsUsed'];
  toolUsageColumns: string[] = ['startTime', 'endTime', 'durationInMinutes', 'unitsUsed'];

  protected closeSession(): void {
    this.detailsStore.closeSession(this.userId);
  }
}
