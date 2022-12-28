import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { coursesMock } from '../../services/courses.mock';
import { CoursesService } from '../../services/courses.service';
import { CoursesComponent } from './courses.component';

describe('CoursesComponent', () => {
  let component: CoursesComponent;
  let fixture: ComponentFixture<CoursesComponent>;
  let courseServiceSpy: jasmine.SpyObj<CoursesService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let activatedRouteSpy: jasmine.SpyObj<ActivatedRoute>;
  let dialogSpy: any;

  beforeEach(async () => {
    courseServiceSpy = jasmine.createSpyObj<CoursesService>('CoursesService', {
      list: of(coursesMock),
      loadById: undefined,
      save: undefined,
      remove: undefined
    });
    routerSpy = jasmine.createSpyObj(['navigate']);
    activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', ['']);
    dialogSpy = jasmine.createSpyObj(['open']);

    await TestBed.configureTestingModule({
      imports: [MatDialogModule, NoopAnimationsModule],
      declarations: [CoursesComponent],
      providers: [
        { provide: CoursesService, useValue: courseServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
        { provide: MatSnackBar, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CoursesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
