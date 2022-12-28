import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { ErrorDialogComponent } from './error-dialog.component';

describe('ErrorDialogComponent', () => {
  let component: ErrorDialogComponent;
  let fixture: ComponentFixture<ErrorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule, ErrorDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: 'Error' },
        { provide: MatDialogRef, useValue: {} }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should launch an error dialog with a message and a Close button', () => {
    const errorMessageDom = fixture.nativeElement.querySelector(
      '.mat-mdc-dialog-content'
    );
    expect(errorMessageDom.textContent).toContain('Error');

    const okBtn = fixture.nativeElement.querySelector('button');
    expect(okBtn.textContent).toContain('Close');
  });
});
