import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { of } from 'rxjs';

import { Course } from '../model/course';
import { CoursesService } from '../services/courses';

export const courseResolver: ResolveFn<Course> = (route: ActivatedRouteSnapshot) => {
  const service = inject(CoursesService);
  if (route.params && route.params['id']) {
    return service.loadById(route.params['id']);
  }
  return of({ _id: '', name: '', category: '', lessons: [] });
};

