export interface TokenAssignment {
  id: string;
  customerId: string;
  tokenId: string;
  tokenType: TokenType;
  status: TokenAssignmentStatus;
  assignmentDate: Date;
  unassignmentDate: Date;
}

export enum TokenAssignmentStatus {
  WaitingForAssignment = 'WAITING_FOR_ASSIGNMENT',
  Assigned = 'ASSIGNED',
  WaitingForUnassignment = 'WAITING_FOR_UNASSIGNMENT',
  Unassigned = 'UNASSIGNED',
}

export enum TokenType {
  Fixed = 'FIXED',
  Temporary = 'TEMPORARY',
}
