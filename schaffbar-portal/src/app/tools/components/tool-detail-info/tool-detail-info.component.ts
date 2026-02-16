import { Component, effect, inject, input, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { TranslatePipe } from '@ngx-translate/core';

import { Tool } from '../../tool.model';

export interface UpdateToolCommand {
  id: string;
  name?: string;
  description?: string;
}

@Component({
  selector: 'schbar-tool-detail-info',
  templateUrl: './tool-detail-info.component.html',
  styleUrl: './tool-detail-info.component.scss',
  imports: [MatInputModule, MatButtonModule, MatIconModule, ReactiveFormsModule, TranslatePipe],
})
export class ToolDetailInfoComponent {
  private fb = inject(NonNullableFormBuilder);

  tool = input.required<Tool>();

  toolUpdated = output<UpdateToolCommand>();

  protected readonly = signal(true);

  toolForm = this.fb.group({
    name: [''],
    description: [''],
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
    });
  }
}
