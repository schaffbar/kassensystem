import { TestBed } from '@angular/core/testing';

import { RfidTagAssignmentService } from './rfid-tag-assignment.service';

describe('RfidTagAssignmentService', () => {
  let service: RfidTagAssignmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RfidTagAssignmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
