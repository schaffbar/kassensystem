import { DatePipe } from '@angular/common';
import { Component, computed, inject, input, output } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';

import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { ConfirmDeleteComponent } from '../../../shared/components/confirm-delete/confirm-delete.component';
import { Tool } from '../../../tools/tool.model';
import { ToolCertification, ToolCertificationStatus } from '../../shared/models/tool-certification.model';

@Component({
  selector: 'schbar-user-tool-certifications',
  templateUrl: './user-tool-certifications.component.html',
  styleUrl: './user-tool-certifications.component.scss',
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatChipsModule, DatePipe, TranslatePipe],
})
export class UserToolCertificationsComponent {
  private readonly dialog = inject(MatDialog);
  private readonly translate = inject(TranslateService);

  certifications = input.required<ToolCertification[]>();
  allTools = input.required<Tool[]>();

  pauseCertification = output<{ customerId: string; toolId: string }>();
  reactivateCertification = output<{ customerId: string; toolId: string }>();
  revokeCertification = output<{ customerId: string; toolId: string }>();

  readonly displayedColumns: string[] = ['toolName', 'status', 'certifiedAt', 'actions'];
  readonly CertificationStatus = ToolCertificationStatus;

  toolNameMap = computed(() => {
    const map = new Map<string, string>();
    for (const t of this.allTools()) {
      map.set(t.id, t.name);
    }
    return map;
  });

  onPause(cert: ToolCertification): void {
    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('users.dialogs.certifications.pauseCertification.title'),
        message: this.translate.instant('users.dialogs.certifications.pauseCertification.message'),
        entity: this.toolNameMap().get(cert.toolId),
        confirmLabel: this.translate.instant('users.dialogs.certifications.pauseCertification.confirm'),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.pauseCertification.emit({ customerId: cert.customerId, toolId: cert.toolId });
      }
    });
  }

  onReactivate(cert: ToolCertification): void {
    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('users.dialogs.certifications.reactivateCertification.title'),
        message: this.translate.instant('users.dialogs.certifications.reactivateCertification.message'),
        entity: this.toolNameMap().get(cert.toolId),
        confirmLabel: this.translate.instant('users.dialogs.certifications.reactivateCertification.confirm'),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.reactivateCertification.emit({ customerId: cert.customerId, toolId: cert.toolId });
      }
    });
  }

  onRevoke(cert: ToolCertification): void {
    const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
      data: {
        title: this.translate.instant('users.dialogs.certifications.revokeCertification.title'),
        message: this.translate.instant('users.dialogs.certifications.revokeCertification.message'),
        entity: this.toolNameMap().get(cert.toolId),
        confirmLabel: this.translate.instant('users.dialogs.certifications.revokeCertification.confirm'),
      },
      minWidth: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.revokeCertification.emit({ customerId: cert.customerId, toolId: cert.toolId });
      }
    });
  }
}
