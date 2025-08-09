import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RfidTagListComponent } from './rfid-tag-list.component';

describe('RfidTagListComponent', () => {
  let component: RfidTagListComponent;
  let fixture: ComponentFixture<RfidTagListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfidTagListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RfidTagListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
