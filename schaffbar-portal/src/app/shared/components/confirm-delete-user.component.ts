import { Component, inject } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogContent, MatDialogModule, MatDialogTitle } from '@angular/material/dialog';

@Component({
  selector: 'schbar-confirm-delete-user',
  templateUrl: './confirm-delete-user.component.html',
  styleUrls: ['./confirm-delete-user.component.scss'],
  imports: [MatDialogModule, MatDialogTitle, MatDialogContent, MatButtonModule],
})
export class ConfirmDeleteUserComponent {
  data = inject(MAT_DIALOG_DATA);
}
