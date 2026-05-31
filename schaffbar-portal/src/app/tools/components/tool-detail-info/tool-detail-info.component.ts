import { LowerCasePipe } from '@angular/common';
import { Component, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { CertificationRequirement, Tool, ToolArea, UpdateToolCommand } from '../../tool.model';

@Component({
  selector: 'schbar-tool-detail-info',
  templateUrl: './tool-detail-info.component.html',
  styleUrl: './tool-detail-info.component.scss',
  imports: [
    LowerCasePipe,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    ReactiveFormsModule,
    TranslatePipe,
  ],
})
export class ToolDetailInfoComponent {
  private fb = inject(NonNullableFormBuilder);

  tool = input.required<Tool>();

  toolUpdated = output<UpdateToolCommand>();

  protected readonly = signal(true);

  protected readonly toolAreas = Object.values(ToolArea);
  protected readonly certificationRequirements = Object.values(CertificationRequirement);

  toolForm = this.fb.group({
    name: [''],
    description: [''],
    area: ['' as ToolArea, [Validators.required]],
    certificationRequirement: ['' as CertificationRequirement, [Validators.required]],
  });

  initialValues = effect(() => this.setInitialFormValues());

  updateTool() {
    this.readonly.set(false);
  }

  onCancel() {
    this.setInitialFormValues();
    this.readonly.set(true);
  }

  onSave() {
    if (this.toolForm.valid) {
      const formValues = this.toolForm.value;
      const command: UpdateToolCommand = {
        id: this.tool().id,
        name: formValues.name,
        description: formValues.description,
        area: formValues.area as ToolArea,
        certificationRequirement: formValues.certificationRequirement as CertificationRequirement,
      };

      this.toolUpdated.emit(command);
      this.readonly.set(true);
    }
  }

  // --------------------------------------------------------------------------
  // helper

  private setInitialFormValues() {
    const tool = this.tool();
    this.toolForm.setValue({
      name: tool.name,
      description: tool.description || '',
      area: tool.area,
      certificationRequirement: tool.certificationRequirement,
    });
  }
}
