import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmationDialog } from './confirmation-dialog';

describe('ConfirmationDialog', () => {
  let component: ConfirmationDialog;
  let fixture: ComponentFixture<ConfirmationDialog>;
  let dialogRefSpy: { close: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    dialogRefSpy = { close: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [ConfirmationDialog],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: 'Are you sure?' },
        { provide: MatDialogRef, useValue: dialogRefSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the message from MAT_DIALOG_DATA', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Are you sure?');
  });

  it('should close dialog with true when Yes button is clicked', () => {
    fixture.nativeElement.querySelector('#yesBtn')?.click();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should close dialog with false when No button is clicked', () => {
    fixture.nativeElement.querySelector('#noBtn')?.click();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });
});
