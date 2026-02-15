import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolDetailInfoComponent } from './tool-detail-info.component';

describe('ToolDetailInfoComponent', () => {
  let component: ToolDetailInfoComponent;
  let fixture: ComponentFixture<ToolDetailInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToolDetailInfoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ToolDetailInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
