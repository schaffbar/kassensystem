import { TestBed } from '@angular/core/testing';

import { RfidReaderDetailStore } from './rfid-reader-detail.store';

describe('RfidReaderDetailStore', () => {
  let store: InstanceType<typeof RfidReaderDetailStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RfidReaderDetailStore],
    });
    store = TestBed.inject(RfidReaderDetailStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
