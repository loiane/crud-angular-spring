import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { EMPTY, catchError, of } from 'rxjs';

import { Course } from '../model/course';
import { CoursesService } from '../services/courses';

export const courseResolver: ResolveFn<Course> = (route: ActivatedRouteSnapshot) => {
  const service = inject(CoursesService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);
  if (route.params && route.params['id']) {
    return service.loadById(route.params['id']).pipe(
      catchError(() => {
        snackBar.open('Could not load the course.', 'X', { duration: 5000 });
        router.navigate(['/courses']);
        return EMPTY;
      })
    );
  }
  return of({ _id: '', name: '', category: '', lessons: [] });
};
