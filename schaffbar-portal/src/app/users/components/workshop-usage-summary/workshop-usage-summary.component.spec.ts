import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkshopUsageSummaryComponent } from './workshop-usage-summary.component';

describe('WorkshopUsageSummaryComponent', () => {
  let component: WorkshopUsageSummaryComponent;
  let fixture: ComponentFixture<WorkshopUsageSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkshopUsageSummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkshopUsageSummaryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
