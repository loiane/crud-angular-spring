import { Routes } from '@angular/router';

import { CourseView } from './components/course-view/course-view';
import { CourseForm } from './containers/course-form/course-form';
import { Courses } from './containers/courses/courses';
import { courseResolver } from './resolver/course-resolver';

export const COURSES_ROUTES: Routes = [
  { path: '', component: Courses },
  { path: 'new', component: CourseForm, resolve: { course: courseResolver } },
  {
    path: 'edit/:id',
    component: CourseForm,
    resolve: { course: courseResolver }
  },
  {
    path: 'view/:id',
    component: CourseView,
    resolve: { course: courseResolver }
  }
];

