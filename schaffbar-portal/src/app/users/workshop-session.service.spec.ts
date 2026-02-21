import { TestBed } from '@angular/core/testing';

import { WorkshopSessionService } from './workshop-session.service';

describe('WorkshopSessionService', () => {
  let service: WorkshopSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkshopSessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
