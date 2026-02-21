import { TestBed } from '@angular/core/testing';

import { ToolDetailStore } from './tool-detail.store';

describe('ToolDetailStore', () => {
  let store: InstanceType<typeof ToolDetailStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ToolDetailStore],
    });
    store = TestBed.inject(ToolDetailStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
