export interface Tool {
  id: string;
  name: string;
  description: string;
  rfidReaderId?: string;
  createdAt: Date;
  updatedAt: Date;
}

// ----------------------------------------------------------------------------
// commands

export interface CreateToolCommand {
  name: string;
  description?: string;
  rfidReaderId?: string;
}

export interface UpdateToolCommand {
  id: string;
  name?: string;
  description?: string;
}

export interface ChangeRfidReaderCommand {
  toolId: string;
  rfidReaderId?: string;
}
