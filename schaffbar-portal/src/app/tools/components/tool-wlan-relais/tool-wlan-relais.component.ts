import { Component, computed, effect, inject, input, output } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { Tool, UpdateWlanRelaisCommand, WLAN_RELAIS_TEMPLATES, WlanRelaisType } from '../../tool.model';

const IP_PATTERN = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;

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
    ReactiveFormsModule,
    TranslatePipe,
  ],
})
export class ToolWlanRelaisComponent {
  private readonly fb = inject(NonNullableFormBuilder);

  tool = input.required<Tool>();

  wlanRelaisUpdated = output<UpdateWlanRelaisCommand>();

  protected readonly wlanRelaisTypes = Object.values(WlanRelaisType);

  protected form = this.fb.group({
    wlanRelaisType: [''],
    ipAddress: ['', [Validators.required, Validators.pattern(IP_PATTERN)]],
  });

  protected derivedCommands = computed(() => {
    const type = this.form.controls.wlanRelaisType.value as WlanRelaisType | '';
    const ip = this.form.controls.ipAddress.value;
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

  syncValues = effect(() => {
    this.resetFormFromTool();
  });

  protected isDirty(): boolean {
    const tool = this.tool();
    return (
      this.form.controls.wlanRelaisType.value !== (tool.wlanRelaisType || '') ||
      this.form.controls.ipAddress.value !== (tool.ipAddress || '')
    );
  }

  protected onCancel(): void {
    this.resetFormFromTool();
  }

  protected onSave(): void {
    const type = this.form.controls.wlanRelaisType.value;
    if (type) {
      this.form.controls.ipAddress.markAsTouched();
      if (this.form.controls.ipAddress.invalid) {
        return;
      }
    }

    const command: UpdateWlanRelaisCommand = {
      toolId: this.tool().id,
      wlanRelaisType: (type as WlanRelaisType) || undefined,
      ipAddress: type ? this.form.controls.ipAddress.value || undefined : undefined,
    };
    this.wlanRelaisUpdated.emit(command);
  }

  private resetFormFromTool(): void {
    const tool = this.tool();
    this.form.reset({
      wlanRelaisType: tool.wlanRelaisType || '',
      ipAddress: tool.ipAddress || '',
    });
  }
}
