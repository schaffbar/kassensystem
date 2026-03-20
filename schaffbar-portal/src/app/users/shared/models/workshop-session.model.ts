export interface WorkshopSession {
  id: string;
  customerId: string;
  startTime: string;
  closeTime: string;
  status: WorkshopSessionStatus;
  workshopUsages: WorkshopUsage[];
  toolUsageSummaries: ToolUsageSummary[];
}

export interface WorkshopUsage {
  id: string;
  entryTime: string;
  exitTime: string;
  durationInMinutes: number;
  unitsUsed: number;
}

export interface ToolUsageSummary {
  toolId: string;
  toolName: string;
  active: boolean;
  totalDurationInMinutes: number;
  totalUnits: number;
  usages: ToolUsage[];
}

export interface ToolUsage {
  id: string;
  startTime: string;
  endTime: string;
  durationInMinutes: number;
  unitsUsed: number;
}

export enum WorkshopSessionStatus {
  Open = 'OPEN',
  Paid = 'PAID',
}
