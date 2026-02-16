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
import {
  ChangeRfidReaderCommand,
  ToolRfidReaderAssignmentComponent,
} from '../../components/tool-rfid-reader-assignment/tool-rfid-reader-assignment.component';
import { ToolsStore } from '../../tools.store';
import { ToolDetailStore } from './tool-detail.store';

@Component({
  selector: 'schbar-tool-detail',
  templateUrl: './tool-detail.component.html',
  styleUrl: './tool-detail.component.scss',
  imports: [
    MatTabsModule,
    MatButtonModule,
    MatIconModule,
    TranslatePipe,
    ToolDetailInfoComponent,
    ToolRfidReaderAssignmentComponent,
  ],
  providers: [ToolDetailStore],
})
export class ToolDetailComponent {
  private readonly detailsStore = inject(ToolDetailStore);
  private readonly rfidReaderStore = inject(RfidReaderStore);
  private readonly toolsStore = inject(ToolsStore);

  id = input.required<string>();

  selectedTool = computed(() => this.detailsStore.tool());
  allRfidReaders = computed(() => this.rfidReaderStore.entities());
  assignedRfidReaderIds = computed(() => {
    const currentToolId = this.selectedTool()?.id;
    const ids = this.toolsStore
      .entities()
      .filter((tool) => tool.id !== currentToolId && tool.rfidReaderId)
      .map((tool) => tool.rfidReaderId!);
    return new Set(ids);
  });

  constructor() {
    this.detailsStore.setToolId(this.id);
    this.rfidReaderStore.loadAllRfidReaders();
    this.toolsStore.loadAllTools();
  }

  protected reloadTool(): void {
    this.detailsStore.reloadTool();
  }

  protected onToolUpdated(command: UpdateToolCommand): void {
    this.detailsStore.updateTool(command);
  }

  protected onRfidReaderChanged(command: ChangeRfidReaderCommand): void {
    this.detailsStore.changeRfidReader(command);
  }
}
