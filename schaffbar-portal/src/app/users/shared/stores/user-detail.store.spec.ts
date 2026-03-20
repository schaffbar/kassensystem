import { TestBed } from '@angular/core/testing';

import { UserDetailStore } from './user-detail.store';

describe('UserDetailStore', () => {
  let store: InstanceType<typeof UserDetailStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserDetailStore],
    });
    store = TestBed.inject(UserDetailStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });
});
