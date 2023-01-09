import { Location } from '@angular/common';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AppMaterialModule } from '../../../shared/app-material/app-material.module';
import { coursesMock } from '../../services/courses.mock';
import { CoursesService } from '../../services/courses.service';
import { CourseFormComponent } from './course-form.component';

describe('CourseFormComponent', () => {
  let component: CourseFormComponent;
  let fixture: ComponentFixture<CourseFormComponent>;
  let courseServiceSpy: jasmine.SpyObj<CoursesService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let activatedRouteMock: any;
  let locationSpy: jasmine.SpyObj<Location>;

  beforeEach(async () => {
    courseServiceSpy = jasmine.createSpyObj<CoursesService>('CoursesService', {
      list: of(coursesMock),
      loadById: undefined,
      save: undefined,
      remove: of(coursesMock[0])
    });
    snackBarSpy = jasmine.createSpyObj<MatSnackBar>(['open']);
    locationSpy = jasmine.createSpyObj<Location>('Location', ['back']);
    activatedRouteMock = {
      snapshot: {
        data: {
          course: coursesMock[0]
        }
      }
    };

    await TestBed.configureTestingModule({
      declarations: [CourseFormComponent],
      imports: [
        MatDialogModule,
        ReactiveFormsModule,
        AppMaterialModule,
        RouterTestingModule.withRoutes([]),
        NoopAnimationsModule
      ],
      providers: [
        { provide: CoursesService, useValue: courseServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Location, useValue: locationSpy },
        { provide: MatDialog }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a form with 3 fields', () => {
    expect(component.form.contains('_id')).toBeTruthy();
    expect(component.form.contains('name')).toBeTruthy();
    expect(component.form.contains('category')).toBeTruthy();
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

  it('should call `CoursesService.save` when the form is submitted', () => {
    const saveSpy = spyOn(component, 'onSubmit');
    const saveButton = fixture.debugElement.nativeElement.querySelector(
      'button[type="submit"]'
    );
    saveButton.click();
    expect(saveSpy).toHaveBeenCalled();
  });

  it('should back the location when cancel is clicked', () => {
    const cancelSpy = spyOn(component, 'onCancel');
    const cancelButton = fixture.debugElement.nativeElement.querySelector(
      'button[type="button"]'
    );
    cancelButton.click();
    expect(cancelSpy).toHaveBeenCalled();
  });

  it('should call location.back when onCancel is called', () => {
    component.onCancel();
    expect(locationSpy.back).toHaveBeenCalled();
  });
});
