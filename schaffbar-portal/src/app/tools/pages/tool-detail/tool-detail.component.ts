import { Component, inject, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReaderStore } from '../../../rfid-readers/rfid-readers.store';
import { ToolDetailStore } from './tool-detail.store';

@Component({
  selector: 'schbar-tool-detail',
  templateUrl: './tool-detail.component.html',
  styleUrl: './tool-detail.component.scss',
  imports: [
    MatTabsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    TranslatePipe,
  ],
  providers: [ToolDetailStore],
})
export class ToolDetailComponent {
  protected readonly detailsStore = inject(ToolDetailStore);
  protected readonly rfidReaderStore = inject(RfidReaderStore);

  id = input.required<string>();

  constructor() {
    this.detailsStore.setToolId(this.id);
    this.rfidReaderStore.loadAllRfidReaders();
  }

  protected reloadTool(): void {
    this.detailsStore.reloadTool();
  }
}
