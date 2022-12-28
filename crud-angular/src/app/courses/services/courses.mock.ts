import { Course } from '../model/course';
import { CoursePage } from '../model/course-page';

export const coursesMock: Course[] = [
  {
    _id: '1',
    name: 'Angular',
    category: 'front-end'
  },
  {
    _id: '2',
    name: 'Java',
    category: 'back-end'
  }
];

export const coursesPageMock: CoursePage = {
  courses: coursesMock,
  totalElements: 2,
  totalPages: 1
};
