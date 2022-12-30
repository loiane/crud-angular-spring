import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { coursesMock } from '../../services/courses.mock';
import { CoursesService } from '../../services/courses.service';
import { CoursesComponent } from './courses.component';
import { ErrorDialogComponent } from '../../../shared/components/error-dialog/error-dialog.component';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';

describe('CoursesComponent', () => {
  let component: CoursesComponent;
  let fixture: ComponentFixture<CoursesComponent>;
  let courseServiceSpy: jasmine.SpyObj<CoursesService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let activatedRouteSpy: jasmine.SpyObj<ActivatedRoute>;
  let loader: HarnessLoader;
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
      imports: [
        MatDialogModule,
        NoopAnimationsModule,
        ErrorDialogComponent,
        ConfirmationDialogComponent
      ],
      declarations: [CoursesComponent],
      providers: [
        { provide: CoursesService, useValue: courseServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
        { provide: MatDialog, useValue: dialogSpy },
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

  it('should create and call ngOnInit', () => {
    courseServiceSpy.list.and.returnValue(of(coursesMock));
    // will trigger ngOnInit
    fixture.detectChanges();
    expect(component).toBeTruthy();
    component.courses$?.subscribe(result => {
      expect(result).toEqual(coursesMock);
    });
  });

  /*it('should display error dialog when courses are not loaded', async () => {
    courseServiceSpy.list.and.returnValue(throwError(''));
    fixture.detectChanges(); // ngOnInit
    component.courses$?.subscribe(result => {
      expect(result).toEqual([]);
      expect(dialogSpy.open as jasmine.Spy).toHaveBeenCalledTimes(2); // 1 time ngOninit
    });
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(2);
    dialogs[0].close(); // close so karma can see all results
    dialogs[1].close();
  });*/

  it('should navigate to new screen when onAdd', () => {
    component.onAdd(); // trigger action
    const spy = routerSpy.navigate as jasmine.Spy;
    expect(spy).toHaveBeenCalledTimes(1);
    const navArgs = spy.calls.first().args;
    expect(navArgs[0]).toEqual(['new']);
    expect(navArgs[1]).toEqual({ relativeTo: activatedRouteSpy });
  });

  it('should navigate to form screen when onEdit', () => {
    const course = { _id: '1', name: '', category: '' };
    component.onEdit(course); // trigger action
    const spy = routerSpy.navigate as jasmine.Spy;
    expect(spy).toHaveBeenCalledTimes(1);
    const navArgs = spy.calls.first().args;
    expect(navArgs[0]).toEqual(['edit', course._id]);
    expect(navArgs[1]).toEqual({ relativeTo: activatedRouteSpy });
  });

  // it('should open ErrorDialogComponent onError', async () => {
  //   loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
  //   fixture.detectChanges();
  //   component.onError('Error');
  //   const dialogs = await loader.getAllHarnesses(MatDialogHarness);
  //   expect(dialogs.length).toBe(1);
  //   dialogs[0].close(); // close so karma can see all results
  // });
});
