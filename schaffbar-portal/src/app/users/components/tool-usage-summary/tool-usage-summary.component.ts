import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';

import { TranslatePipe } from '@ngx-translate/core';

import { ToolUsageSummary } from '../../shared/models/workshop-session.model';

@Component({
  selector: 'schbar-tool-usage-summary',
  templateUrl: './tool-usage-summary.component.html',
  styleUrl: './tool-usage-summary.component.scss',
  imports: [MatTableModule, MatChipsModule, DatePipe, TranslatePipe],
})
export class ToolUsageSummaryComponent {
  summary = input.required<ToolUsageSummary>();

  readonly toolUsageColumns: string[] = ['startTime', 'endTime', 'durationInMinutes', 'unitsUsed'];
}
