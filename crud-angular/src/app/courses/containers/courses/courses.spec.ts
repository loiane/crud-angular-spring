import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { of } from 'rxjs';
import { Courses } from './courses';
import { CoursePage } from '../../model/course-page';
import { CoursesList } from '../../components/courses-list/courses-list';

const mockPage: CoursePage = {
  courses: [
    { _id: '1', name: 'Angular', category: 'front-end' },
    { _id: '2', name: 'Spring', category: 'back-end' }
  ],
  totalElements: 2
};

describe('Courses container', () => {
  let component: Courses;
  let fixture: ComponentFixture<Courses>;
  let httpMock: HttpTestingController;
  let router: Router;
  let dialog: MatDialog;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Courses],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Courses);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    dialog = fixture.debugElement.injector.get(MatDialog);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  const flushInitialRequest = async () => {
    const req = httpMock.expectOne(r => r.url === '/api/courses');
    req.flush(mockPage);
    await fixture.whenStable();
    fixture.detectChanges();
  };

  const mockDialogRef = (result: boolean) =>
    ({ afterClosed: () => of(result) }) as unknown as MatDialogRef<unknown, boolean>;

  it('should create', async () => {
    await flushInitialRequest();
    expect(component).toBeTruthy();
  });

  it('should make an initial GET request to /api/courses', async () => {
    const req = httpMock.expectOne(r => r.url === '/api/courses');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('should render the courses table once resource has data', async () => {
    await flushInitialRequest();
    const rows = fixture.nativeElement.querySelectorAll('mat-row');
    expect(rows.length).toBe(2);
  });

  it('should navigate to new when the add button is clicked', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const addBtn = fixture.nativeElement.querySelector('button[aria-label="Add a new Course"]');
    addBtn?.click();
    expect(spy).toHaveBeenCalledWith(['new'], expect.any(Object));
  });

  it('should navigate to edit when edit button in template is clicked', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const editBtn = fixture.nativeElement.querySelector('button[aria-label="Update Course"]');
    editBtn?.click();
    expect(spy).toHaveBeenCalledWith(['edit', '1'], expect.any(Object));
  });

  it('should navigate to view when the course name link is clicked', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const nameLink = fixture.nativeElement.querySelector('mat-cell a');
    nameLink?.click();
    expect(spy).toHaveBeenCalledWith(['view', '1'], expect.any(Object));
  });

  it('should open confirmation dialog when remove button in template is clicked', async () => {
    await flushInitialRequest();
    const dialogSpy = vi.spyOn(dialog, 'open').mockReturnValue(mockDialogRef(false));
    const removeBtn = fixture.nativeElement.querySelector('button[aria-label="Remove Course"]');
    removeBtn?.click();
    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should invoke (remove) template binding via triggerEventHandler', async () => {
    await flushInitialRequest();
    const dialogSpy = vi.spyOn(dialog, 'open').mockReturnValue(mockDialogRef(false));
    const listDebug = fixture.debugElement.query(By.directive(CoursesList));
    listDebug?.triggerEventHandler('remove', mockPage.courses[0]);
    await Promise.resolve();
    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should request the new page when the paginator emits a page event', async () => {
    await flushInitialRequest();
    const paginatorDebug = fixture.debugElement.query(By.directive(MatPaginator));
    paginatorDebug?.triggerEventHandler('page', { pageIndex: 1, pageSize: 5, length: 20 });
    await new Promise(resolve => setTimeout(resolve));
    const reqs = httpMock.match(r => r.url === '/api/courses');
    expect(
      reqs.some(
        r => r.request.params.get('page') === '1' && r.request.params.get('pageSize') === '5'
      )
    ).toBe(true);
  });

  it('should open error dialog when loading courses fails', async () => {
    const dialogSpy = vi.spyOn(dialog, 'open');
    httpMock.expectOne(r => r.url === '/api/courses').error(new ProgressEvent('error'));
    await fixture.whenStable();
    fixture.detectChanges();
    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should DELETE course and reload when removal is confirmed', async () => {
    await flushInitialRequest();
    vi.spyOn(dialog, 'open').mockReturnValue(mockDialogRef(true));
    const listDebug = fixture.debugElement.query(By.directive(CoursesList));
    listDebug?.triggerEventHandler('remove', mockPage.courses[0]);
    await Promise.resolve();

    const deleteReq = httpMock.expectOne(r => r.url === '/api/courses/1');
    expect(deleteReq.request.method).toBe('DELETE');
    deleteReq.flush({});
    await new Promise(resolve => setTimeout(resolve));
    httpMock.match(r => r.url === '/api/courses');
  });

  it('should not DELETE when removal is cancelled', async () => {
    await flushInitialRequest();
    vi.spyOn(dialog, 'open').mockReturnValue(mockDialogRef(false));
    const listDebug = fixture.debugElement.query(By.directive(CoursesList));
    listDebug?.triggerEventHandler('remove', mockPage.courses[0]);
    await fixture.whenStable();
    httpMock.expectNone('/api/courses/1');
  });

  it('should show error dialog when DELETE fails', async () => {
    await flushInitialRequest();
    const dialogOpenSpy = vi.spyOn(dialog, 'open').mockReturnValue(mockDialogRef(true));
    const listDebug = fixture.debugElement.query(By.directive(CoursesList));
    listDebug?.triggerEventHandler('remove', mockPage.courses[0]);
    await Promise.resolve();

    httpMock.expectOne(r => r.url === '/api/courses/1').error(new ProgressEvent('error'));
    await fixture.whenStable();
    expect(dialogOpenSpy).toHaveBeenCalledTimes(2);
  });
});
