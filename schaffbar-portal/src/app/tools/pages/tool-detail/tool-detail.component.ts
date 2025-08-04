import { Component, inject, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';

import { ToolDetailStore } from './tool-detail.store';

@Component({
  selector: 'schbar-tool-detail',
  templateUrl: './tool-detail.component.html',
  styleUrl: './tool-detail.component.scss',
  imports: [MatTabsModule, MatButtonModule, MatIconModule, MatInputModule, ReactiveFormsModule],
  providers: [ToolDetailStore],
})
export class ToolDetailComponent {
  protected readonly detailsStore = inject(ToolDetailStore);

  id = input.required<string>();

  constructor() {
    this.detailsStore.setToolId(this.id);
  }

  protected reloadTool(): void {
    this.detailsStore.reloadTool();
  }
}
