import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ConfirmationDialogComponent } from './confirmation-dialog.component';

describe('ConfirmationDialogComponent', () => {
  let fixture: ComponentFixture<ConfirmationDialogComponent>;

  beforeEach(async () => {
    const matDialogRefMock = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [MatDialogModule, ConfirmationDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: 'Some title.' },
        { provide: MatDialogRef, useValue: matDialogRefMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationDialogComponent);
    fixture.detectChanges();
  });

  it('should launch an error dialog with a message and a Yes button', () => {
    const errorMessageDom = fixture.nativeElement.querySelector(
      '.mat-mdc-dialog-content'
    );
    expect(errorMessageDom.textContent).toContain('Some title.');

    const okBtn = fixture.nativeElement.querySelector('button');
    expect(okBtn.textContent).toContain('Yes');
  });

  it('should close the dialog when clicking on the Yes button', () => {
    const okBtn = fixture.nativeElement.querySelector('button');
    okBtn.click();
    fixture.detectChanges();

    const dialog = fixture.debugElement.injector.get(MatDialogRef);
    expect(dialog.close).toHaveBeenCalledWith(true);
  });

  it('should close the dialog when clicking on the No button', () => {
    const noBtn = fixture.nativeElement.querySelectorAll('button')[1];
    noBtn.click();
    fixture.detectChanges();

    const dialog = fixture.debugElement.injector.get(MatDialogRef);
    expect(dialog.close).toHaveBeenCalledWith(false);
  });
});
