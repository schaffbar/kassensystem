import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolUsageSummaryComponent } from './tool-usage-summary.component';

describe('ToolUsageSummaryComponent', () => {
  let component: ToolUsageSummaryComponent;
  let fixture: ComponentFixture<ToolUsageSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToolUsageSummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ToolUsageSummaryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
