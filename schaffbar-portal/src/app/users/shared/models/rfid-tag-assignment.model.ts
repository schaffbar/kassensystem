export interface RfidTagAssignment {
  id: string;
  customerId: string;
  rfidTagId: string;
  assignmentType: RfidTagAssignmentType;
  status: RfidTagAssignmentStatus;
  assignmentDate: Date;
  unassignmentDate: Date;
}

export enum RfidTagAssignmentStatus {
  WaitingForAssignment = 'WAITING_FOR_ASSIGNMENT',
  Assigned = 'ASSIGNED',
  WaitingForUnassignment = 'WAITING_FOR_UNASSIGNMENT',
  Unassigned = 'UNASSIGNED',
}

export enum RfidTagAssignmentType {
  Fixed = 'FIXED',
  Temporary = 'TEMPORARY',
}
