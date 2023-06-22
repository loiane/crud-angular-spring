import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormArray } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { Course } from '../../model/course';
import { coursesMock, coursesPageMock } from '../../services/courses.mock';
import { CoursesService } from '../../services/courses.service';
import { CourseFormComponent } from './course-form.component';

describe('CourseFormComponent', () => {
  let component: CourseFormComponent;
  let fixture: ComponentFixture<CourseFormComponent>;
  let courseServiceSpy: jasmine.SpyObj<CoursesService>;
  let activatedRouteMock: any;
  let loader: HarnessLoader;

  beforeEach(async () => {
    courseServiceSpy = jasmine.createSpyObj<CoursesService>('CoursesService', {
      list: of(coursesPageMock),
      loadById: undefined,
      save: of(coursesMock[0]),
      remove: of(coursesMock[0])
    });
    activatedRouteMock = {
      snapshot: {
        data: {
          course: coursesMock[0]
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [
        MatDialogModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        NoopAnimationsModule,
        CourseFormComponent
      ],
      providers: [
        { provide: CoursesService, useValue: courseServiceSpy },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a form with 4 fields', () => {
    expect(component.form.contains('_id')).toBeTruthy();
    expect(component.form.contains('name')).toBeTruthy();
    expect(component.form.contains('category')).toBeTruthy();
    expect(component.form.contains('lessons')).toBeTruthy();
  });

  it('should have a form with a `name` field and 3 validators', () => {
    const nameControl = component.form.get('name');
    nameControl?.setValue('');
    expect(nameControl?.valid).toBeFalsy();
    expect(nameControl?.errors?.['required']).toBeTruthy();
    nameControl?.setValue('a');
    expect(nameControl?.errors?.['minlength']).toBeTruthy();
    nameControl?.setValue('a'.repeat(101));
    expect(nameControl?.errors?.['maxlength']).toBeTruthy();
    nameControl?.setValue('a'.repeat(5));
    expect(nameControl?.errors).toBeNull();
  });

  it('should have a form with a `category` field and one validator', () => {
    const categoryControl = component.form.get('category');
    categoryControl?.setValue('');
    expect(categoryControl?.valid).toBeFalsy();
    expect(categoryControl?.errors?.['required']).toBeTruthy();
    categoryControl?.setValue('a');
    expect(categoryControl?.errors).toBeNull();
  });

  it('should have a form with a `lessons` field and one validator', () => {
    component.removeLesson(0);
    const lessonsControl = component.form.get('lessons');
    expect(lessonsControl?.valid).toBeFalsy();
    expect(lessonsControl?.errors?.['required']).toBeTruthy();
    component.addLesson();
    expect(lessonsControl?.errors).toBeNull();
  });

  it('should return the error message then the `name` field is invalid', () => {
    const nameControl = component.form.get('name');
    nameControl?.setValue('');
    expect(component.getErrorMessage('name')).toEqual('Field is required.');
    nameControl?.setValue('a');
    expect(component.getErrorMessage('name')).toEqual(
      'Field cannot be less than 5 characters long.'
    );
    nameControl?.setValue('a'.repeat(101));
    expect(component.getErrorMessage('name')).toEqual(
      'Field cannot be more than 100 characters long.'
    );
  });

  it('should return the error message then the `category` field is invalid', () => {
    const categoryControl = component.form.get('category');
    categoryControl?.setValue('');
    expect(component.getErrorMessage('category')).toEqual('Field is required.');
  });

  it('should return the error message then the `lessons.name` field is invalid', () => {
    const formArray = component.form.get('lessons') as UntypedFormArray;
    const lessonNameControl = formArray.controls[0].get('name');
    lessonNameControl?.setValue('');
    expect(component.getLessonErrorMessage('name', 0)).toEqual('Field is required.');

    lessonNameControl?.setValue('a');
    expect(component.getLessonErrorMessage('name', 0)).toEqual(
      'Field cannot be less than 5 characters long.'
    );

    lessonNameControl?.setValue('a'.repeat(101));
    expect(component.getLessonErrorMessage('name', 0)).toEqual(
      'Field cannot be more than 100 characters long.'
    );
  });

  it('should return the error message then the `lessons.youtubeUrl` field is invalid', () => {
    const formArray = component.form.get('lessons') as UntypedFormArray;
    const lessonUrlControl = formArray.controls[0].get('youtubeUrl');
    lessonUrlControl?.setValue('');
    expect(component.getLessonErrorMessage('youtubeUrl', 0)).toEqual(
      'Field is required.'
    );

    lessonUrlControl?.setValue('a');
    expect(component.getLessonErrorMessage('youtubeUrl', 0)).toEqual(
      'Field cannot be less than 10 characters long.'
    );

    lessonUrlControl?.setValue('a'.repeat(101));
    expect(component.getLessonErrorMessage('youtubeUrl', 0)).toEqual(
      'Field cannot be more than 11 characters long.'
    );
  });

  it('should call `CoursesService.save` when the form is submitted', () => {
    const saveSpy = spyOn(component, 'onSubmit');
    const saveButton = fixture.debugElement.nativeElement.querySelector(
      'button[type="submit"]'
    );
    saveButton.dispatchEvent(new Event('click'));
    expect(saveSpy).toHaveBeenCalled();
  });

  it('should back the location when cancel is clicked', () => {
    const cancelSpy = spyOn(component, 'onCancel');
    const cancelButton = fixture.debugElement.nativeElement.querySelector(
      'button[type="button"]'
    );
    cancelButton.dispatchEvent(new Event('click'));
    expect(cancelSpy).toHaveBeenCalled();
  });

  it('should call `CoursesService.save` when onSubmit is called and form is valid', () => {
    component.form.setValue(coursesMock[0]);
    component.onSubmit();
    expect(courseServiceSpy.save).toHaveBeenCalled();
  });

  it('should call formUtils.validateAllFormFields when onSubmit is called and form is invalid', () => {
    const validateAllFormFieldsSpy = spyOn(
      component.formUtils,
      'validateAllFormFields'
    ).and.callThrough();
    activatedRouteMock.snapshot.data.course = {
      _id: '',
      name: '',
      category: '',
      lessons: undefined
    } as Course;
    component.ngOnInit();
    component.onSubmit();
    expect(validateAllFormFieldsSpy).toHaveBeenCalled();
  });

  it('should call onError and open dialog when onSubmit save fails', async () => {
    courseServiceSpy.save.and.returnValue(throwError(() => new Error('test')));
    component.form.setValue(coursesMock[0]);
    component.onSubmit();
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(1);
    await dialogs[0].close(); // close the dialog
  });

  it('should load empty form when no course is passed', () => {
    activatedRouteMock.snapshot.data.course = {
      _id: '',
      name: '',
      category: '',
      lessons: undefined
    } as Course;
    component.ngOnInit();
    expect(component.form.value).toEqual({
      _id: '',
      name: '',
      category: '',
      lessons: [{ _id: '', name: '', youtubeUrl: '' }]
    });
  });
});
