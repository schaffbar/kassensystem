import { DatePipe } from '@angular/common';
import { Component, computed, input, output } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { TranslatePipe } from '@ngx-translate/core';

import { ActiveUser } from '../../dashboard.model';

@Component({
  selector: 'schbar-active-users-widget',
  templateUrl: './active-users-widget.component.html',
  styleUrl: './active-users-widget.component.scss',
  imports: [MatButtonModule, MatCardModule, MatTableModule, MatIconModule, DatePipe, TranslatePipe],
})
export class ActiveUsersWidgetComponent {
  readonly activeUsers = input.required<ActiveUser[]>();

  readonly reload = output<void>();
  readonly userSelected = output<ActiveUser>();

  protected displayedColumns = ['name', 'entryTime', 'duration'];
  protected activeUsersCount = computed(() => this.activeUsers().length);
  protected dataSource = computed(() => {
    return new MatTableDataSource<ActiveUser>(this.activeUsers());
  });

  protected reloadActiveUsers(): void {
    this.reload.emit();
  }

  protected selectUser(user: ActiveUser): void {
    this.userSelected.emit(user);
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
}
