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

  beforeEach(async () => {
    courseServiceSpy = jasmine.createSpyObj<CoursesService>('CoursesService', {
      list: of(coursesMock),
      loadById: undefined,
      save: undefined,
      remove: of(coursesMock[0])
    });
    snackBarSpy = jasmine.createSpyObj<MatSnackBar>(['open']);
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
});
