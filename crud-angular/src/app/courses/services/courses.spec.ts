import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CoursesService } from './courses';
import { Course } from '../model/course';

const mockCourse: Course = { _id: '1', name: 'Angular', category: 'front-end', lessons: [] };

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

  describe('loadById', () => {
    it('should GET /api/courses/:id', () => {
      let result: Course | undefined;
      service.loadById('1').subscribe(c => (result = c));
      const req = httpMock.expectOne('/api/courses/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockCourse);
      expect(result).toEqual(mockCourse);
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
