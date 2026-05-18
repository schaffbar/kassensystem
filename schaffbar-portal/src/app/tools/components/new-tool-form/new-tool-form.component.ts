import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { TranslatePipe } from '@ngx-translate/core';

import { CreateToolCommand } from '../../tool.model';
import { ToolsStore } from '../../tools.store';

@Component({
  selector: 'schbar-new-tool-form',
  templateUrl: './new-tool-form.component.html',
  styleUrl: './new-tool-form.component.scss',
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    MatFormFieldModule,
    MatSelectModule,
    TranslatePipe,
  ],
})
export class NewToolFormComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly dialogRef = inject(MatDialogRef<NewToolFormComponent>);
  private readonly toolsStore = inject(ToolsStore);

  protected toolForm = this.fb.group({
    name: ['', [Validators.required, this.nameExistsValidator()]],
    description: [''],
  });

  protected save(): void {
    if (this.toolForm.invalid) {
      this.toolForm.markAllAsTouched();
      return;
    }

    const formValue = this.toolForm.value;

    const command: CreateToolCommand = {
      name: formValue.name!,
      description: formValue.description || undefined,
    };

    this.dialogRef.close(command);
  }

  private nameExistsValidator(): (control: AbstractControl) => ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const exists = this.toolsStore.entities().some((tool) => tool.name.toLowerCase() === control.value.toLowerCase());
      return exists ? { nameExists: true } : null;
    };
  }
}
