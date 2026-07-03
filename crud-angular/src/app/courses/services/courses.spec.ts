import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CoursesService } from './courses';
import { Course } from '../model/course';
import { CoursePage } from '../model/course-page';

const mockCourse: Course = { _id: '1', name: 'Angular', category: 'front-end', lessons: [] };
const mockPage: CoursePage = { courses: [mockCourse], totalElements: 1 };

describe('CoursesService', () => {
  let service: CoursesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(CoursesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('list', () => {
    it('should GET /api/courses with page params', () => {
      service.list(0, 10).subscribe(page => {
        expect(page.courses).toEqual([mockCourse]);
        expect(page.totalElements).toBe(1);
      });
      const req = httpMock.expectOne(r => r.url === '/api/courses');
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('page')).toBe('0');
      expect(req.request.params.get('pageSize')).toBe('10');
      req.flush(mockPage);
    });
  });

  describe('loadById', () => {
    it('should GET /api/courses/:id when cache is empty', () => {
      service.loadById('1').subscribe();
      const req = httpMock.expectOne('/api/courses/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockCourse);
    });

    it('should return course from cache without HTTP call', () => {
      // Populate cache via list()
      service.list().subscribe();
      httpMock.expectOne(r => r.url === '/api/courses').flush(mockPage);

      let result: Course | undefined;
      service.loadById('1').subscribe(c => (result = c));
      expect(result).toEqual(mockCourse);
      httpMock.expectNone('/api/courses/1');
    });

    it('should fall back to HTTP when id not found in cache', () => {
      service.list().subscribe();
      httpMock.expectOne(r => r.url === '/api/courses').flush(mockPage);

      service.loadById('999').subscribe();
      const req = httpMock.expectOne('/api/courses/999');
      req.flush({ _id: '999', name: 'Other', category: 'back-end' });
    });
  });

  describe('save', () => {
    it('should POST to create a course when _id is absent', () => {
      service.save({ name: 'New', category: 'back-end' }).subscribe();
      const req = httpMock.expectOne('/api/courses');
      expect(req.request.method).toBe('POST');
      req.flush({ _id: '2', name: 'New', category: 'back-end' });
    });

    it('should PUT to update a course when _id is present', () => {
      service.save(mockCourse).subscribe();
      const req = httpMock.expectOne('/api/courses/1');
      expect(req.request.method).toBe('PUT');
      req.flush(mockCourse);
    });
  });

  describe('remove', () => {
    it('should DELETE /api/courses/:id', () => {
      service.remove('1').subscribe();
      const req = httpMock.expectOne('/api/courses/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(mockCourse);
    });
  });
});
