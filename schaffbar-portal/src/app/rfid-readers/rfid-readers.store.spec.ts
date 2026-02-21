import { TestBed } from '@angular/core/testing';

import { RfidReaderStore } from './rfid-readers.store';

describe('RfidReaderStore', () => {
  let store: InstanceType<typeof RfidReaderStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(RfidReaderStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
