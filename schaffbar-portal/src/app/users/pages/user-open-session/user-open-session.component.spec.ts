import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserOpenSessionComponent } from './user-open-session.component';

describe('UserOpenSessionComponent', () => {
  let component: UserOpenSessionComponent;
  let fixture: ComponentFixture<UserOpenSessionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserOpenSessionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserOpenSessionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
