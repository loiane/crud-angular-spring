import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
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

const buildModule = async (course: Course) => {
  await TestBed.configureTestingModule({
    imports: [CourseForm],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      provideRouter([]),
      { provide: ActivatedRoute, useValue: { snapshot: { data: { course } } } }
    ]
  }).compileComponents();
};

describe('CourseForm — new course', () => {
  let component: CourseForm;
  let fixture: ComponentFixture<CourseForm>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await buildModule(emptyCourse);
    fixture = TestBed.createComponent(CourseForm);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => { expect(component).toBeTruthy(); });

  it('should initialise form with empty values', () => {
    const form = (component as any).form;
    expect(form.get('name').value).toBe('');
    expect(form.get('category').value).toBe('');
  });

  it('should create one default lesson row', () => {
    const lessons = (component as any).form.get('lessons').controls;
    expect(lessons.length).toBe(1);
  });

  it('should add a lesson on addLesson()', () => {
    (component as any).addLesson();
    expect((component as any).form.get('lessons').controls.length).toBe(2);
  });

  it('should remove a lesson on removeLesson()', () => {
    (component as any).addLesson();
    (component as any).removeLesson(1);
    expect((component as any).form.get('lessons').controls.length).toBe(1);
  });

  it('should not make HTTP call when form is invalid on submit', async () => {
    await (component as any).onSubmit();
    httpMock.expectNone('/api/courses');
  });

  it('should display name error in template when field is touched and invalid', () => {
    (component as any).form.get('name').markAsTouched();
    fixture.detectChanges();
    const errors = fixture.nativeElement.querySelectorAll('mat-error');
    expect(errors.length).toBeGreaterThan(0);
  });

  it('getLessonFormArray should return controls', () => {
    const arr = (component as any).getLessonFormArray();
    expect(Array.isArray(arr)).toBe(true);
    expect(arr.length).toBe(1);
  });

  it('getErrorMessage should return required error', () => {
    (component as any).form.get('name').markAsTouched();
    expect((component as any).getErrorMessage('name')).toBe('Field is required.');
  });

  it('getLessonErrorMessage should return string', () => {
    const msg = (component as any).getLessonErrorMessage('name', 0);
    expect(typeof msg).toBe('string');
  });
});

describe('CourseForm — edit course', () => {
  let component: CourseForm;
  let fixture: ComponentFixture<CourseForm>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await buildModule(existingCourse);
    fixture = TestBed.createComponent(CourseForm);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should populate form with existing course values', () => {
    const form = (component as any).form;
    expect(form.get('name').value).toBe('Angular Basics');
    expect(form.get('category').value).toBe('front-end');
  });

  it('should load existing lessons into the form array', () => {
    const lessons = (component as any).form.get('lessons').controls;
    expect(lessons.length).toBe(1);
    expect(lessons[0].get('name').value).toBe('Intro Lesson');
  });

  it('should call snackBar and navigate back on successful save', async () => {
    const snackBarSpy = vi.spyOn((component as any).snackBar, 'open');
    const locationSpy = vi.spyOn((component as any).location, 'back');
    const submitPromise = (component as any).onSubmit();
    httpMock.expectOne('/api/courses/1').flush(existingCourse);
    await submitPromise;
    expect(snackBarSpy).toHaveBeenCalled();
    expect(locationSpy).toHaveBeenCalled();
  });

  it('should PUT on valid form submit', async () => {
    const submitPromise = (component as any).onSubmit();
    const req = httpMock.expectOne('/api/courses/1');
    expect(req.request.method).toBe('PUT');
    req.flush(existingCourse);
    await submitPromise;
  });

  it('should open error dialog when save throws', async () => {
    const dialogSpy = vi.spyOn((component as any).dialog, 'open');
    const submitPromise = (component as any).onSubmit();
    httpMock.expectOne('/api/courses/1').error(new ProgressEvent('error'));
    await submitPromise;
    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should call location.back() on onCancel()', () => {
    const spy = vi.spyOn((component as any).location, 'back');
    (component as any).onCancel();
    expect(spy).toHaveBeenCalled();
  });
});
