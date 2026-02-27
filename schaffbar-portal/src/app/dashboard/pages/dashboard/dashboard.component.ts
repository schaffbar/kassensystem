import { Component } from '@angular/core';

import { TranslatePipe } from '@ngx-translate/core';

import { ActiveUsersWidgetComponent } from '../../components/active-users-widget/active-users-widget.component';

@Component({
  selector: 'schbar-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  imports: [ActiveUsersWidgetComponent, TranslatePipe],
})
export class DashboardComponent {}
