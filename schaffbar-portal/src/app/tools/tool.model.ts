export interface Tool {
  id: string;
  name: string;
  description: string;
  rfidReaderId?: string;
  createdAt: Date;
  updatedAt: Date;
}
