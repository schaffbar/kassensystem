import { Component, inject } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogContent, MatDialogModule, MatDialogTitle } from '@angular/material/dialog';

import { TranslatePipe } from '@ngx-translate/core';

export interface ConfirmDeleteDialogData {
  title: string;
  message: string;
  entity?: string;
  confirmLabel?: string;
}

@Component({
  selector: 'schbar-confirm-delete',
  templateUrl: './confirm-delete.component.html',
  styleUrl: './confirm-delete.component.scss',
  imports: [MatDialogModule, MatDialogTitle, MatDialogContent, MatButtonModule, TranslatePipe],
})
export class ConfirmDeleteComponent {
  data: ConfirmDeleteDialogData = inject(MAT_DIALOG_DATA);
}
