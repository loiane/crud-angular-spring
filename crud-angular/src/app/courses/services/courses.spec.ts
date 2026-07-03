import { TestBed } from '@angular/core/testing';

import { Courses } from './courses';

describe('Courses', () => {
  let service: Courses;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Courses);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
