import { TestBed } from '@angular/core/testing';

import { ToolsStore } from './tools.store';

describe('ToolsStore', () => {
  let store: InstanceType<typeof ToolsStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(ToolsStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
