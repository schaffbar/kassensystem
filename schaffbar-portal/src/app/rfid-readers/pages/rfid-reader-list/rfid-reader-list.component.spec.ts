import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RfidReaderListComponent } from './rfid-reader-list.component';

describe('RfidReaderListComponent', () => {
  let component: RfidReaderListComponent;
  let fixture: ComponentFixture<RfidReaderListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfidReaderListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RfidReaderListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
