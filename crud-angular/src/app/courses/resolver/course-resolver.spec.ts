import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { courseResolver } from './course-resolver';
import { CoursesService } from '../services/courses';
import { Course } from '../model/course';

describe('courseResolver', () => {
  let coursesService: CoursesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    coursesService = TestBed.inject(CoursesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should return an empty course when route has no id', () => {
    const route = { params: {} } as ActivatedRouteSnapshot;
    let result: Course | undefined;
    TestBed.runInInjectionContext(() => {
      (courseResolver(route, null as any) as any).subscribe((c: Course) => (result = c));
    });
    expect(result!._id).toBe('');
    expect(result!.name).toBe('');
    expect(result!.lessons).toEqual([]);
  });

  it('should call CoursesService.loadById when route has an id', () => {
    const mockCourse: Course = { _id: '42', name: 'Test', category: 'back-end' };
    const spy = vi.spyOn(coursesService, 'loadById');
    const route = { params: { id: '42' } } as unknown as ActivatedRouteSnapshot;
    TestBed.runInInjectionContext(() => {
      courseResolver(route, null as any);
    });
    expect(spy).toHaveBeenCalledWith('42');
  });
});
