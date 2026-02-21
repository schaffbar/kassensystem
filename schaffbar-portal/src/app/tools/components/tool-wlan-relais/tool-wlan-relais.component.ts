import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { Tool, UpdateWlanRelaisCommand, WLAN_RELAIS_TEMPLATES, WlanRelaisType } from '../../tool.model';

@Component({
  selector: 'schbar-tool-wlan-relais',
  templateUrl: './tool-wlan-relais.component.html',
  styleUrl: './tool-wlan-relais.component.scss',
  imports: [
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    TranslatePipe,
  ],
})
export class ToolWlanRelaisComponent {
  tool = input.required<Tool>();

  wlanRelaisUpdated = output<UpdateWlanRelaisCommand>();

  protected readonly wlanRelaisTypes = Object.values(WlanRelaisType);

  protected selectedType = signal<WlanRelaisType | ''>('');
  protected ipAddress = signal('');

  protected derivedCommands = computed(() => {
    const type = this.selectedType();
    const ip = this.ipAddress();
    if (!type) {
      return { httpStartCommand: '', onCommand: '', offCommand: '' };
    }

    const template = WLAN_RELAIS_TEMPLATES[type];

    return {
      httpStartCommand: template.httpStartCommand.replace('{{ipAddress}}', ip || ''),
      onCommand: template.onCommand,
      offCommand: template.offCommand,
    };
  });

  protected isDirty = computed(() => {
    const tool = this.tool();
    const currentType = tool.wlanRelaisType || '';
    const currentIp = tool.ipAddress || '';
    return this.selectedType() !== currentType || this.ipAddress() !== currentIp;
  });

  protected isValid = computed(() => {
    const type = this.selectedType();
    if (!type) {
      return true; // clearing is valid
    }
    return !!this.ipAddress();
  });

  syncValues = effect(() => {
    this.setCurrentValuesFromTool();
  });

  protected onCancel(): void {
    this.setCurrentValuesFromTool();
  }

  protected onSave(): void {
    const type = this.selectedType();
    const command: UpdateWlanRelaisCommand = {
      toolId: this.tool().id,
      wlanRelaisType: type || undefined,
      ipAddress: type ? this.ipAddress() || undefined : undefined,
    };
    this.wlanRelaisUpdated.emit(command);
  }

  private setCurrentValuesFromTool(): void {
    const tool = this.tool();
    this.selectedType.set(tool.wlanRelaisType || '');
    this.ipAddress.set(tool.ipAddress || '');
  }
}
