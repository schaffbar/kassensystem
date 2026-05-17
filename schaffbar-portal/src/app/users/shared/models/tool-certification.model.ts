export interface ToolCertification {
  id: string;
  customerId: string;
  toolId: string;
  toolName: string;
  status: ToolCertificationStatus;
  certifiedAt: string;
  certifiedBy: string;
  updatedAt: string;
}

export enum ToolCertificationStatus {
  Active = 'ACTIVE',
  Paused = 'PAUSED',
  Revoked = 'REVOKED',
}

export interface BatchCreateToolCertificationCommand {
  customerIds: string[];
  toolId: string;
  certifiedBy: string;
}
