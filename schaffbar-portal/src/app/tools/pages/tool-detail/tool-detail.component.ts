import { Component, computed, inject, input } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

import { TranslatePipe } from '@ngx-translate/core';

import { RfidReaderStore } from '../../../rfid-readers/rfid-readers.store';
import { BatchCreateToolCertificationCommand } from '../../../users/shared/models/tool-certification.model';
import { UsersStore } from '../../../users/shared/stores/users.store';
import { ToolCertificationsComponent } from '../../components/tool-certifications/tool-certifications.component';
import { ToolDetailInfoComponent } from '../../components/tool-detail-info/tool-detail-info.component';
import { ToolInstructorsComponent } from '../../components/tool-instructors/tool-instructors.component';
import { ToolRfidReaderAssignmentComponent } from '../../components/tool-rfid-reader-assignment/tool-rfid-reader-assignment.component';
import { ToolWlanRelaisComponent } from '../../components/tool-wlan-relais/tool-wlan-relais.component';
import { ChangeRfidReaderCommand, SetWlanRelaisCommand, UpdateToolCommand } from '../../tool.model';
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
    ToolInstructorsComponent,
    ToolRfidReaderAssignmentComponent,
    ToolWlanRelaisComponent,
    ToolCertificationsComponent,
  ],
  providers: [ToolDetailStore],
})
export class ToolDetailComponent {
  private readonly detailsStore = inject(ToolDetailStore);
  private readonly rfidReaderStore = inject(RfidReaderStore);
  private readonly toolsStore = inject(ToolsStore);
  private readonly usersStore = inject(UsersStore);

  id = input.required<string>();

  selectedTool = computed(() => this.detailsStore.tool());
  toolCertifications = computed(() => this.detailsStore.toolCertifications());
  allUsers = computed(() => this.usersStore.entities());
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
    this.usersStore.loadAllUsers();
  }

  protected reloadTool(): void {
    this.detailsStore.reloadTool();
    this.rfidReaderStore.loadAllRfidReaders();
    this.toolsStore.loadAllTools();
  }

  protected onToolUpdated(command: UpdateToolCommand): void {
    this.detailsStore.updateTool(command);
  }

  protected onRfidReaderChanged(command: ChangeRfidReaderCommand): void {
    this.detailsStore.changeRfidReader(command);
  }

  protected onRfidReaderCleared(toolId: string): void {
    this.detailsStore.clearRfidReader(toolId);
  }

  protected onWlanRelaisUpdated(command: SetWlanRelaisCommand): void {
    this.detailsStore.setWlanRelais(command);
  }

  protected onWlanRelaisCleared(toolId: string): void {
    this.detailsStore.clearWlanRelais(toolId);
  }

  protected onBatchCreateCertifications(command: BatchCreateToolCertificationCommand): void {
    this.detailsStore.batchCreateCertifications(command);
  }

  protected onDeleteCertification(event: { customerId: string; toolId: string }): void {
    this.detailsStore.deleteCertification(event);
  }

  protected onInstructorsAdded(event: { toolId: string; instructorIds: string[] }): void {
    this.detailsStore.addInstructors(event);
  }

  protected onInstructorsRemoved(event: { toolId: string; instructorIds: string[] }): void {
    this.detailsStore.removeInstructors(event);
  }
}
