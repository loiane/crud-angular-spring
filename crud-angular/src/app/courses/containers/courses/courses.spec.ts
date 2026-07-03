import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';
import { MatPaginator } from '@angular/material/paginator';
import { of } from 'rxjs';
import { Courses } from './courses';
import { CoursePage } from '../../model/course-page';
import { Course } from '../../model/course';
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
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  const flushInitialRequest = async () => {
    const req = httpMock.expectOne(r => r.url === '/api/courses');
    req.flush(mockPage);
    await fixture.whenStable();
    fixture.detectChanges();
  };

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

  it('should navigate to edit when edit button in template is clicked', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const editBtn = fixture.nativeElement.querySelector('button[aria-label="Update Course"]');
    editBtn?.click();
    expect(spy).toHaveBeenCalledWith(['edit', '1'], expect.any(Object));
  });

  it('should open confirmation dialog when remove button in template is clicked', async () => {
    await flushInitialRequest();
    const dialogSpy = vi.spyOn((component as any).dialog, 'open').mockReturnValue({
      afterClosed: () => of(false)
    });
    const removeBtn = fixture.nativeElement.querySelector('button[aria-label="Remove Course"]');
    removeBtn?.click();
    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should invoke (remove) template binding via triggerEventHandler', async () => {
    await flushInitialRequest();
    const dialogSpy = vi.spyOn((component as any).dialog, 'open').mockReturnValue({
      afterClosed: () => of(false)
    });
    const listDebug = fixture.debugElement.query(By.directive(CoursesList));
    listDebug?.triggerEventHandler('remove', mockPage.courses[0]);
    await Promise.resolve();
    expect(dialogSpy).toHaveBeenCalled();
  });

  it('should invoke (page) template binding via paginator triggerEventHandler', async () => {
    await flushInitialRequest();
    const paginatorDebug = fixture.debugElement.query(By.directive(MatPaginator));
    paginatorDebug?.triggerEventHandler('page', { pageIndex: 1, pageSize: 5, length: 20 });
    expect((component as any).pageIndex()).toBe(1);
    httpMock.match(r => r.url === '/api/courses');
  });

  it('should update pageIndex and pageSize signals on onPageChange()', async () => {
    await flushInitialRequest();
    (component as any).onPageChange({ pageIndex: 2, pageSize: 5, length: 20 });
    expect((component as any).pageIndex()).toBe(2);
    expect((component as any).pageSize()).toBe(5);
    httpMock.match(r => r.url === '/api/courses');
  });

  it('should navigate to new on onAdd()', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    (component as any).onAdd();
    expect(spy).toHaveBeenCalledWith(['new'], expect.objectContaining({}));
  });

  it('should navigate to edit/:id on onEdit()', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const course: Course = { _id: '1', name: 'Angular', category: 'front-end' };
    (component as any).onEdit(course);
    expect(spy).toHaveBeenCalledWith(['edit', '1'], expect.objectContaining({}));
  });

  it('should navigate to view/:id on onView()', async () => {
    await flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const course: Course = { _id: '1', name: 'Angular', category: 'front-end' };
    (component as any).onView(course);
    expect(spy).toHaveBeenCalledWith(['view', '1'], expect.objectContaining({}));
  });

  it('should open error dialog on onError()', async () => {
    await flushInitialRequest();
    const dialog = (component as any).dialog;
    const spy = vi.spyOn(dialog, 'open');
    (component as any).onError('Test error');
    expect(spy).toHaveBeenCalled();
  });

  it('should DELETE course and reload when removal is confirmed', async () => {
    await flushInitialRequest();
    const dialogRefMock = { afterClosed: () => of(true) };
    vi.spyOn((component as any).dialog, 'open').mockReturnValue(dialogRefMock);

    const removePromise = (component as any).onRemove(mockPage.courses[0]);
    await Promise.resolve();

    httpMock.expectOne(r => r.url === '/api/courses/1').flush({});
    await removePromise;
    httpMock.match(r => r.url === '/api/courses');
  });

  it('should not DELETE when removal is cancelled', async () => {
    await flushInitialRequest();
    const dialogRefMock = { afterClosed: () => of(false) };
    vi.spyOn((component as any).dialog, 'open').mockReturnValue(dialogRefMock);

    await (component as any).onRemove(mockPage.courses[0]);
    httpMock.expectNone('/api/courses/1');
  });

  it('should show error dialog when DELETE fails', async () => {
    await flushInitialRequest();
    const dialogRefMock = { afterClosed: () => of(true) };
    const dialogOpenSpy = vi.spyOn((component as any).dialog, 'open').mockReturnValue(dialogRefMock);

    const removePromise = (component as any).onRemove(mockPage.courses[0]);
    await Promise.resolve();

    httpMock.expectOne(r => r.url === '/api/courses/1').error(new ProgressEvent('error'));
    await removePromise;
    expect(dialogOpenSpy).toHaveBeenCalled();
    httpMock.match(() => true);
  });
});
