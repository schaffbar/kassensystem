import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolWlanRelaisComponent } from './tool-wlan-relais.component';

describe('ToolWlanRelaisComponent', () => {
  let component: ToolWlanRelaisComponent;
  let fixture: ComponentFixture<ToolWlanRelaisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToolWlanRelaisComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ToolWlanRelaisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
