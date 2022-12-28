import { TestBed } from '@angular/core/testing';
import { CoursesService } from '../services/courses.service';

import { CourseResolver } from './course.resolver';

describe('CourseResolver', () => {
  let resolver: CourseResolver;
  let courseServiceMock: any;

  beforeEach(() => {
    const courseServiceSpy = jasmine.createSpyObj('CoursesService', ['loadById']);
    TestBed.configureTestingModule({
      providers: [{ provide: CoursesService, useValue: courseServiceSpy }]
    });
    resolver = TestBed.inject(CourseResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
