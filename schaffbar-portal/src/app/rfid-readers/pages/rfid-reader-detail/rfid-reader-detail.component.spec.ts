import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RfidReaderDetailComponent } from './rfid-reader-detail.component';

describe('RfidReaderDetailComponent', () => {
  let component: RfidReaderDetailComponent;
  let fixture: ComponentFixture<RfidReaderDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfidReaderDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RfidReaderDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
