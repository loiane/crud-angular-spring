import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { provideRouter } from '@angular/router';
import { CourseForm } from './course-form';
import { Course } from '../../model/course';

const emptyCourse: Course = { _id: '', name: '', category: '' };
const existingCourse: Course = {
  _id: '1',
  name: 'Angular Basics',
  category: 'front-end',
  lessons: [{ _id: 'l1', name: 'Intro Lesson', youtubeUrl: 'abcdefghij' }]
};

const buildModule = async () => {
  await TestBed.configureTestingModule({
    imports: [CourseForm],
    providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
  }).compileComponents();
};

const lessonNameInputs = (fixture: ComponentFixture<CourseForm>): NodeListOf<HTMLInputElement> =>
  fixture.nativeElement.querySelectorAll('td.lesson-name-cell input');

describe('CourseForm — new course', () => {
  let component: CourseForm;
  let fixture: ComponentFixture<CourseForm>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await buildModule();
    fixture = TestBed.createComponent(CourseForm);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('course', emptyCourse);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render an empty form with one default lesson row', () => {
    const nameInput: HTMLInputElement = fixture.nativeElement.querySelector('input');
    expect(nameInput.value).toBe('');
    expect(lessonNameInputs(fixture).length).toBe(1);
  });

  it('should add a lesson row when the add button is clicked', async () => {
    const addBtn = fixture.nativeElement.querySelector('mat-toolbar button');
    addBtn.click();
    await fixture.whenStable();
    expect(lessonNameInputs(fixture).length).toBe(2);
  });

  it('should remove a lesson row when the remove button is clicked', async () => {
    fixture.nativeElement.querySelector('mat-toolbar button').click();
    await fixture.whenStable();
    expect(lessonNameInputs(fixture).length).toBe(2);

    fixture.nativeElement.querySelector('button.danger-action').click();
    await fixture.whenStable();
    expect(lessonNameInputs(fixture).length).toBe(1);
  });

  it('should not make HTTP call when form is invalid on submit', async () => {
    fixture.nativeElement.querySelector('button[type="submit"]').click();
    await fixture.whenStable();
    httpMock.expectNone('/api/courses');
  });

  it('should display validation errors after an invalid submit', async () => {
    fixture.nativeElement.querySelector('button[type="submit"]').click();
    await fixture.whenStable();
    fixture.detectChanges();
    const errors = fixture.nativeElement.querySelectorAll('mat-error');
    expect(errors.length).toBeGreaterThan(0);
    expect(errors[0].textContent).toContain('Field is required.');
  });

  it('should update the model when typing into the name input', async () => {
    const nameInput: HTMLInputElement = fixture.nativeElement.querySelector('input');
    nameInput.value = 'Angular Signals';
    nameInput.dispatchEvent(new Event('input'));
    await fixture.whenStable();

    fixture.nativeElement.querySelector('button[type="submit"]').click();
    await fixture.whenStable();
    fixture.detectChanges();
    // name is now valid — its mat-error should not render
    const nameField = fixture.nativeElement.querySelector('mat-form-field');
    expect(nameField.querySelector('mat-error')).toBeNull();
  });
});

describe('CourseForm — edit course', () => {
  let fixture: ComponentFixture<CourseForm>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await buildModule();
    fixture = TestBed.createComponent(CourseForm);
    fixture.componentRef.setInput('course', existingCourse);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  afterEach(() => httpMock.verify());

  it('should populate the form with existing course values', () => {
    const nameInput: HTMLInputElement = fixture.nativeElement.querySelector('input');
    expect(nameInput.value).toBe('Angular Basics');
    const lessonInput = lessonNameInputs(fixture)[0];
    expect(lessonInput.value).toBe('Intro Lesson');
  });

  it('should PUT on valid form submit', async () => {
    fixture.nativeElement.querySelector('button[type="submit"]').click();
    const req = httpMock.expectOne('/api/courses/1');
    expect(req.request.method).toBe('PUT');
    req.flush(existingCourse);
    await fixture.whenStable();
  });

  it('should call snackBar and navigate back on successful save', async () => {
    const snackBarSpy = vi.spyOn(fixture.debugElement.injector.get(MatSnackBar), 'open');
    const locationSpy = vi.spyOn(TestBed.inject(Location), 'back');

    fixture.nativeElement.querySelector('button[type="submit"]').click();
    httpMock.expectOne('/api/courses/1').flush(existingCourse);
    await fixture.whenStable();

    expect(snackBarSpy).toHaveBeenCalled();
    expect(locationSpy).toHaveBeenCalled();
  });

  it('should open error dialog when save throws', async () => {
    const dialogSpy = vi.spyOn(fixture.debugElement.injector.get(MatDialog), 'open');

    fixture.nativeElement.querySelector('button[type="submit"]').click();
    httpMock.expectOne('/api/courses/1').error(new ProgressEvent('error'));
    await fixture.whenStable();

    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should call location.back() when Cancel is clicked', () => {
    const spy = vi.spyOn(TestBed.inject(Location), 'back');
    fixture.nativeElement.querySelector('button[type="button"].btn-space').click();
    expect(spy).toHaveBeenCalled();
  });
});
