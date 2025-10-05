export interface WorkshopSession {
  id: string;
  customerId: string;
  startTime: string;
  closeTime: string;
  status: WorkshopSessionStatus;
  workshopUsages: WorkshopUsage[];
}

export interface WorkshopUsage {
  id: string;
  entryTime: string;
  exitTime: string;
  durationInMinutes: number;
}

export enum WorkshopSessionStatus {
  Open = 'OPEN',
  Paid = 'PAID',
}
