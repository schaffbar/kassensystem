import { Component, computed, effect, inject, input, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { SetWlanRelaisCommand, Tool, WLAN_RELAIS_TEMPLATES, WlanRelaisType } from '../../tool.model';

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

  wlanRelaisUpdated = output<SetWlanRelaisCommand>();
  wlanRelaisCleared = output<string>();

  protected readonly wlanRelaisTypes = Object.values(WlanRelaisType);

  protected form = this.fb.group({
    wlanRelaisType: [''],
    ipAddress: ['', [Validators.required, Validators.pattern(IP_PATTERN)]],
  });

  private formValues = toSignal(this.form.valueChanges, { initialValue: this.form.getRawValue() });

  protected derivedCommands = computed(() => {
    const values = this.formValues();
    const type = (values.wlanRelaisType || '') as WlanRelaisType | '';
    const ip = values.ipAddress || '';
    if (!type) {
      return { httpStartCommand: '', onCommand: '', offCommand: '' };
    }
    const template = WLAN_RELAIS_TEMPLATES[type];
    return {
      httpStartCommand: template.httpStartCommand.replace('{{ipAddress}}', ip),
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

      const command: SetWlanRelaisCommand = {
        toolId: this.tool().id,
        wlanRelaisType: type as WlanRelaisType,
        ipAddress: this.form.controls.ipAddress.value,
      };
      this.wlanRelaisUpdated.emit(command);
    } else {
      this.wlanRelaisCleared.emit(this.tool().id);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private resetFormFromTool(): void {
    const tool = this.tool();
    this.form.reset({
      wlanRelaisType: tool.wlanRelaisType || '',
      ipAddress: tool.ipAddress || '',
    });
  }
}
