import { TestBed } from '@angular/core/testing';

import { UsersStore } from './users.store';

describe('UsersStore', () => {
  let store: InstanceType<typeof UsersStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(UsersStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
