import { Component, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { UpdateRfidReaderCommand } from '../../rfid-reader.model';
import { RfidReaderDetailInfoComponent } from '../rfid-reader-detail-info/rfid-reader-detail-info.component';
import { RfidReaderDetailStore } from './rfid-reader-detail.store';

@Component({
  selector: 'schbar-rfid-reader-detail',
  templateUrl: './rfid-reader-detail.component.html',
  styleUrl: './rfid-reader-detail.component.scss',
  imports: [RfidReaderDetailInfoComponent, MatTabsModule, MatButtonModule, MatChipsModule, MatIconModule],
  providers: [RfidReaderDetailStore],
})
export class RfidReaderDetailComponent {
  protected readonly detailsStore = inject(RfidReaderDetailStore);

  id = input.required<string>();

  constructor() {
    this.detailsStore.setRfidReaderId(this.id);
  }

  protected reloadRfidReader(): void {
    this.detailsStore.reloadRfidReader();
  }

  protected onReaderChanged(command: UpdateRfidReaderCommand): void {
    this.detailsStore.updateRfidReader(command);
  }
}
