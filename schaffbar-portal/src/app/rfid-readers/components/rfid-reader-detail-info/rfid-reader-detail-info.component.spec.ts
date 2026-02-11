import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RfidReaderDetailInfoComponent } from './rfid-reader-detail-info.component';

describe('RfidReaderDetailInfoComponent', () => {
  let component: RfidReaderDetailInfoComponent;
  let fixture: ComponentFixture<RfidReaderDetailInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfidReaderDetailInfoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RfidReaderDetailInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
