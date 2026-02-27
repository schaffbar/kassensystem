import { DatePipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { TranslatePipe } from '@ngx-translate/core';

import { ROUTE } from '../../../app.routes';
import { ActiveUser } from '../../dashboard.model';
import { DashboardStore } from '../../dashboard.store';

@Component({
  selector: 'schbar-active-users-widget',
  templateUrl: './active-users-widget.component.html',
  styleUrl: './active-users-widget.component.scss',
  imports: [MatButtonModule, MatCardModule, MatTableModule, MatIconModule, DatePipe, TranslatePipe],
})
export class ActiveUsersWidgetComponent {
  readonly store = inject(DashboardStore);
  private readonly router = inject(Router);

  protected displayedColumns = ['name', 'entryTime', 'duration'];
  protected activeUsersCount = computed(() => this.store.entities().length);
  protected dataSource = computed(() => {
    return new MatTableDataSource<ActiveUser>(this.store.entities());
  });

  protected reloadActiveUsers(): void {
    this.store.loadActiveUsers();
  }

  protected getDuration(entryTime: string): string {
    const entry = new Date(entryTime);
    const now = new Date();
    const diffMs = now.getTime() - entry.getTime();
    const totalMinutes = Math.floor(diffMs / 60000);
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    return hours > 0 ? `${hours}h ${minutes}m` : `${minutes}m`;
  }

  protected openUserSession(user: ActiveUser): void {
    this.router.navigate([ROUTE.USERS, user.customerId], { queryParams: { tab: 'open-session' } });
  }
}
