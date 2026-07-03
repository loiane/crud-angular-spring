import { Routes } from '@angular/router';

import { Courses } from './containers/courses/courses';
import { courseResolver } from './resolver/course-resolver';

export const COURSES_ROUTES: Routes = [
  { path: '', component: Courses },
  {
    path: 'new',
    loadComponent: () =>
      import('./containers/course-form/course-form').then(m => m.CourseForm),
    resolve: { course: courseResolver }
  },
  {
    path: 'edit/:id',
    loadComponent: () =>
      import('./containers/course-form/course-form').then(m => m.CourseForm),
    resolve: { course: courseResolver }
  },
  {
    path: 'view/:id',
    loadComponent: () =>
      import('./components/course-view/course-view').then(m => m.CourseView),
    resolve: { course: courseResolver }
  }
];
