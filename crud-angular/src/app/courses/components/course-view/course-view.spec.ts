import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { CourseView } from './course-view';
import { Course } from '../../model/course';
import { Lesson } from '../../model/lesson';

const mockLesson1: Lesson = { _id: 'l1', name: 'Lesson 1', youtubeUrl: 'abc123xy' };
const mockLesson2: Lesson = { _id: 'l2', name: 'Lesson 2', youtubeUrl: 'def456yz' };
const mockCourse: Course = {
  _id: '1',
  name: 'Angular',
  category: 'front-end',
  lessons: [mockLesson1, mockLesson2]
};

describe('CourseView', () => {
  let component: CourseView;
  let fixture: ComponentFixture<CourseView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseView],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { data: { course: mockCourse } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CourseView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set course signal from route data on ngOnInit', () => {
    expect((component as any).course()).toEqual(mockCourse);
  });

  it('should set selectedLesson to first lesson on init', () => {
    expect((component as any).selectedLesson()).toEqual(mockLesson1);
  });

  it('should update selectedLesson signal when display() is called', () => {
    (component as any).display(mockLesson2);
    expect((component as any).selectedLesson()).toEqual(mockLesson2);
  });

  it('should return true for displaySelectedLesson with the active lesson', () => {
    (component as any).selectedLesson.set(mockLesson1);
    expect((component as any).displaySelectedLesson(mockLesson1)).toBe(true);
  });

  it('should return false for displaySelectedLesson with a different lesson', () => {
    (component as any).selectedLesson.set(mockLesson1);
    expect((component as any).displaySelectedLesson(mockLesson2)).toBe(false);
  });
});
