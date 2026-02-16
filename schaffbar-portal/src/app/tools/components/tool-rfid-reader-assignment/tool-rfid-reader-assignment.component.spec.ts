import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolRfidReaderAssignmentComponent } from './tool-rfid-reader-assignment.component';

describe('ToolRfidReaderAssignmentComponent', () => {
  let component: ToolRfidReaderAssignmentComponent;
  let fixture: ComponentFixture<ToolRfidReaderAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToolRfidReaderAssignmentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ToolRfidReaderAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
