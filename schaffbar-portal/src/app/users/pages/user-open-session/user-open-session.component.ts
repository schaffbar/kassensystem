import { DatePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';

import { MatTableModule } from '@angular/material/table';

import { WorkshopSessionService } from '../../workshop-session.service';
import { UserOpenSessionStore } from './user-open-session.store';

@Component({
  selector: 'schbar-user-open-session',
  templateUrl: './user-open-session.component.html',
  styleUrl: './user-open-session.component.scss',
  imports: [MatTableModule, DatePipe],
  providers: [UserOpenSessionStore, WorkshopSessionService],
})
export class UserOpenSessionComponent {
  protected readonly openSessionStore = inject(UserOpenSessionStore);

  userId = input.required<string>();

  displayedColumns: string[] = ['entryTime', 'exitTime', 'durationInMinutes'];

  constructor() {
    this.openSessionStore.setUserId(this.userId);
  }
}
