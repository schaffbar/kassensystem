import { TestBed } from '@angular/core/testing';

import { RfidTagService } from './rfid-tags.service';

describe('RfidTagService', () => {
  let service: RfidTagService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RfidTagService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
