import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { TranslatePipe } from '@ngx-translate/core';

import { ROUTE } from '../../../app.routes';
import { ActiveUsersWidgetComponent } from '../../components/active-users-widget/active-users-widget.component';
import { ActiveUser } from '../../dashboard.model';
import { DashboardStore } from '../../dashboard.store';

@Component({
  selector: 'schbar-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  imports: [ActiveUsersWidgetComponent, TranslatePipe],
  providers: [DashboardStore],
})
export class DashboardComponent {
  readonly store = inject(DashboardStore);
  private readonly router = inject(Router);

  protected reloadActiveUsers(): void {
    this.store.loadActiveUsers();
  }

  protected openUserSession(user: ActiveUser): void {
    this.router.navigate([ROUTE.USERS, user.customerId], { queryParams: { tab: 'open-session' } });
  }
}
