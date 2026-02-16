import { Component, computed, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReaderStore } from '../../../rfid-readers/rfid-readers.store';
import {
  ToolDetailInfoComponent,
  UpdateToolCommand,
} from '../../components/tool-detail-info/tool-detail-info.component';
import { ToolDetailStore } from './tool-detail.store';

@Component({
  selector: 'schbar-tool-detail',
  templateUrl: './tool-detail.component.html',
  styleUrl: './tool-detail.component.scss',
  imports: [MatTabsModule, MatButtonModule, MatIconModule, TranslatePipe, ToolDetailInfoComponent],
  providers: [ToolDetailStore],
})
export class ToolDetailComponent {
  private readonly detailsStore = inject(ToolDetailStore);
  private readonly rfidReaderStore = inject(RfidReaderStore);

  id = input.required<string>();

  selectedTool = computed(() => this.detailsStore.tool());
  allRfidReaders = computed(() => this.rfidReaderStore.entities());
  assignedRfidReader = computed(() => {
    const readerId = this.selectedTool()?.rfidReaderId;
    return this.allRfidReaders().find((reader) => reader.id === readerId);
  });

  constructor() {
    this.detailsStore.setToolId(this.id);
    this.rfidReaderStore.loadAllRfidReaders();
  }

  protected reloadTool(): void {
    this.detailsStore.reloadTool();
  }

  protected onToolChanged(command: UpdateToolCommand): void {
    this.detailsStore.updateTool(command);
  }
}
