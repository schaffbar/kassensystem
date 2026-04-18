import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { TranslateModule } from '@ngx-translate/core';

import { RfidReaderType } from '../../rfid-reader.model';
import { ChangeRfidReaderTypeFormComponent } from './change-rfid-reader-type-form.component';

describe('ChangeRfidReaderTypeFormComponent', () => {
  let component: ChangeRfidReaderTypeFormComponent;
  let fixture: ComponentFixture<ChangeRfidReaderTypeFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeRfidReaderTypeFormComponent, TranslateModule.forRoot()],
      providers: [
        { provide: MatDialogRef, useValue: { close: () => {} } },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            rfidReader: { id: '1', macAddress: 'AA:BB', type: RfidReaderType.SwitchBox, name: '', socketName: '' },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ChangeRfidReaderTypeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
