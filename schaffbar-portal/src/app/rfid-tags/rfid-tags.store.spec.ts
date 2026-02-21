import { TestBed } from '@angular/core/testing';

import { RfidTagStore } from './rfid-tags.store';

describe('RfidTagStore', () => {
  let store: InstanceType<typeof RfidTagStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(RfidTagStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
