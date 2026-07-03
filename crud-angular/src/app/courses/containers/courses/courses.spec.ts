import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { Courses } from './courses';
import { CoursePage } from '../../model/course-page';
import { Course } from '../../model/course';

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

  const flushInitialRequest = () => {
    const req = httpMock.expectOne(r => r.url === '/api/courses');
    req.flush(mockPage);
    fixture.detectChanges();
  };

  it('should create', () => {
    flushInitialRequest();
    expect(component).toBeTruthy();
  });

  it('should make an initial GET request to /api/courses', () => {
    const req = httpMock.expectOne(r => r.url === '/api/courses');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('should update pageIndex and pageSize signals on onPageChange()', () => {
    flushInitialRequest();
    (component as any).onPageChange({ pageIndex: 2, pageSize: 5, length: 20 });
    expect((component as any).pageIndex()).toBe(2);
    expect((component as any).pageSize()).toBe(5);
    // consume any pending request triggered by signal change (timing may vary)
    httpMock.match(r => r.url === '/api/courses');
  });

  it('should navigate to new on onAdd()', () => {
    flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    (component as any).onAdd();
    expect(spy).toHaveBeenCalledWith(['new'], expect.objectContaining({}));
  });

  it('should navigate to edit/:id on onEdit()', () => {
    flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const course: Course = { _id: '1', name: 'Angular', category: 'front-end' };
    (component as any).onEdit(course);
    expect(spy).toHaveBeenCalledWith(['edit', '1'], expect.objectContaining({}));
  });

  it('should navigate to view/:id on onView()', () => {
    flushInitialRequest();
    const spy = vi.spyOn(router, 'navigate');
    const course: Course = { _id: '1', name: 'Angular', category: 'front-end' };
    (component as any).onView(course);
    expect(spy).toHaveBeenCalledWith(['view', '1'], expect.objectContaining({}));
  });

  it('should open error dialog on onError()', () => {
    flushInitialRequest();
    const dialog = (component as any).dialog;
    const spy = vi.spyOn(dialog, 'open');
    (component as any).onError('Test error');
    expect(spy).toHaveBeenCalled();
  });
});
