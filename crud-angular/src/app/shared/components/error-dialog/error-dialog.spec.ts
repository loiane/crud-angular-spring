import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ErrorDialog } from './error-dialog';

describe('ErrorDialog', () => {
  let component: ErrorDialog;
  let fixture: ComponentFixture<ErrorDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorDialog],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: 'Something went wrong' }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the error message from MAT_DIALOG_DATA', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Something went wrong');
  });

  it('should display "Error!" title', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Error!');
  });
});
