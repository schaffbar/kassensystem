import { DatePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';

import { TranslatePipe } from '@ngx-translate/core';

import { UserDetailStore } from '../user-detail/user-detail.store';

@Component({
  selector: 'schbar-user-open-session',
  templateUrl: './user-open-session.component.html',
  styleUrl: './user-open-session.component.scss',
  imports: [MatTableModule, MatButtonModule, DatePipe, TranslatePipe],
})
export class UserOpenSessionComponent {
  protected readonly detailsStore = inject(UserDetailStore);

  userId = input.required<string>();

  displayedColumns: string[] = ['entryTime', 'exitTime', 'durationInMinutes'];

  protected closeSession(): void {
    this.detailsStore.closeSession(this.userId);
  }
}
