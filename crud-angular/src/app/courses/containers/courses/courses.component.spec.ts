import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { ErrorDialogComponent } from '../../../shared/components/error-dialog/error-dialog.component';
import { coursesPageMock } from '../../services/courses.mock';
import { CoursesService } from '../../services/courses.service';
import { CoursesComponent } from './courses.component';

describe('CoursesComponent', () => {
  let component: CoursesComponent;
  let fixture: ComponentFixture<CoursesComponent>;
  let courseServiceSpy: jasmine.SpyObj<CoursesService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let activatedRouteSpy: jasmine.SpyObj<ActivatedRoute>;
  let loader: HarnessLoader;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    courseServiceSpy = jasmine.createSpyObj<CoursesService>('CoursesService', {
      list: of(coursesPageMock),
      loadById: undefined,
      save: undefined,
      remove: of(coursesPageMock.courses[0])
    });
    routerSpy = jasmine.createSpyObj(['navigate']);
    activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', ['']);
    snackBarSpy = jasmine.createSpyObj<MatSnackBar>(['open']);

    await TestBed.configureTestingModule({
      imports: [
        MatDialogModule,
        MatSnackBarModule,
        NoopAnimationsModule,
        ErrorDialogComponent,
        ConfirmationDialogComponent,
        CoursesComponent
      ],
      providers: [
        { provide: CoursesService, useValue: courseServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
        { provide: MatDialog },
        { provide: MatSnackBar, useValue: snackBarSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CoursesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create and call ngOnInit', () => {
    courseServiceSpy.list.and.returnValue(of(coursesPageMock));
    // will trigger ngOnInit
    fixture.detectChanges();
    expect(component).toBeTruthy();
    component.courses$?.subscribe(result => {
      expect(result).toEqual(coursesPageMock);
    });
  });

  it('should display error dialog when courses are not loaded', async () => {
    courseServiceSpy.list.and.returnValue(throwError(() => new Error('test')));
    spyOn(component, 'onError');
    fixture.detectChanges(); // ngOnInit
    expect(component.onError).toHaveBeenCalled();
  });

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

  it('should open ErrorDialogComponent onError', async () => {
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    fixture.detectChanges();
    component.onError('Error');
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(1);
    dialogs[0].close(); // close so karma can see all results
  });

  it('should open ConfirmationDialogComponent onRemove', async () => {
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    fixture.detectChanges();
    component.onRemove(coursesPageMock.courses[0]);
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(1);
    dialogs[0].close(); // close so karma can see all results
  });

  it('should remove course and display success message', async () => {
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    fixture.detectChanges();
    spyOn(component, 'refresh');
    component.onRemove(coursesPageMock.courses[0]);
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(1);
    const button = document.getElementById('yesBtn');
    await button?.click();
    expect(courseServiceSpy.remove).toHaveBeenCalledTimes(1);
    expect(component.refresh).toHaveBeenCalledTimes(1);
    //expect(snackBarSpy.open as jasmine.Spy).toHaveBeenCalledTimes(1);
  });

  it('should not remove course if No button was clicked', async () => {
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    fixture.detectChanges();
    component.onRemove(coursesPageMock.courses[0]);
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(1);
    const button = document.getElementById('noBtn');
    await button?.click();
    expect(courseServiceSpy.remove).toHaveBeenCalledTimes(0);
  });

  it('should display error if course could not be removed', async () => {
    courseServiceSpy.remove.and.returnValue(throwError(() => new Error('test')));
    loader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    spyOn(component, 'onError');
    fixture.detectChanges();
    component.onRemove(coursesPageMock.courses[0]);
    const dialogs = await loader.getAllHarnesses(MatDialogHarness);
    expect(dialogs.length).toBe(1);
    const button = document.getElementById('yesBtn');
    await button?.click();
    expect(courseServiceSpy.remove).toHaveBeenCalledTimes(1);
    expect(component.onError).toHaveBeenCalledTimes(1);
  });
});
