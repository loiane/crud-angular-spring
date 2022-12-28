import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { CoursesService } from '../services/courses.service';
import { CourseResolver } from './course.resolver';
import { Course } from '../model/course';

describe('CourseResolver', () => {
  let resolver: CourseResolver;
  let courseServiceSpy: jasmine.SpyObj<CoursesService>;

  beforeEach(() => {
    courseServiceSpy = jasmine.createSpyObj('CoursesService', ['loadById']);
    TestBed.configureTestingModule({
      providers: [{ provide: CoursesService, useValue: courseServiceSpy }]
    });
    resolver = TestBed.inject(CourseResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should return course', () => {
    const course = {
      _id: '1',
      name: 'Angular',
      category: 'Angular description',
      lessons: []
    };
    courseServiceSpy.loadById.and.returnValue(of(course));
    const result = resolver.resolve({ params: { id: 1 } } as any, {} as any);
    result.subscribe((res: Course) => expect(res).toEqual(course));
  });

  it('should return empty course if new', () => {
    const course = { _id: '', name: '', category: '', lessons: [] };
    courseServiceSpy.loadById.and.returnValue(of(course));
    const result = resolver.resolve({ params: {} } as any, {} as any);
    result.subscribe((res: Course) => expect(res).toEqual(course));
  });
});
