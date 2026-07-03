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
const courseWithoutLessons: Course = { _id: '2', name: 'No Lessons', category: 'back-end' };

const buildComponent = async (course: Course) => {
  await TestBed.configureTestingModule({
    imports: [CourseView],
    providers: [
      { provide: ActivatedRoute, useValue: { snapshot: { data: { course } } } }
    ]
  }).compileComponents();

  const fixture = TestBed.createComponent(CourseView);
  fixture.detectChanges();
  return { fixture, component: fixture.componentInstance };
};

describe('CourseView', () => {
  it('should create', async () => {
    const { component } = await buildComponent(mockCourse);
    expect(component).toBeTruthy();
  });

  it('should set course signal from route data', async () => {
    const { component } = await buildComponent(mockCourse);
    expect((component as any).course()).toEqual(mockCourse);
  });

  it('should set selectedLesson to first lesson when lessons exist', async () => {
    const { component } = await buildComponent(mockCourse);
    expect((component as any).selectedLesson()).toEqual(mockLesson1);
  });

  it('should set selectedLesson to null when course has no lessons', async () => {
    const { component } = await buildComponent(courseWithoutLessons);
    expect((component as any).selectedLesson()).toBeNull();
  });

  it('should update selectedLesson signal when display() is called', async () => {
    const { component } = await buildComponent(mockCourse);
    (component as any).display(mockLesson2);
    expect((component as any).selectedLesson()).toEqual(mockLesson2);
  });

  it('should return true for displaySelectedLesson with the active lesson', async () => {
    const { component } = await buildComponent(mockCourse);
    (component as any).selectedLesson.set(mockLesson1);
    expect((component as any).displaySelectedLesson(mockLesson1)).toBe(true);
  });

  it('should return false for displaySelectedLesson with a different lesson', async () => {
    const { component } = await buildComponent(mockCourse);
    (component as any).selectedLesson.set(mockLesson1);
    expect((component as any).displaySelectedLesson(mockLesson2)).toBe(false);
  });

  it('should not throw when onResize is called without a youTubePlayer element', async () => {
    const { component } = await buildComponent(mockCourse);
    expect(() => (component as any).onResize()).not.toThrow();
  });

  it('should call onResize when window resize event fires', async () => {
    const { component } = await buildComponent(mockCourse);
    const spy = vi.spyOn(component as any, 'onResize');
    window.dispatchEvent(new Event('resize'));
    expect(spy).toHaveBeenCalled();
  });

  it('should set videoWidth and videoHeight when youTubePlayer element is present', async () => {
    const { component } = await buildComponent(mockCourse);
    const mockEl = { nativeElement: { clientWidth: 1000 } };
    (component as any).youTubePlayer = () => mockEl;
    (component as any).onResize();
    expect((component as any).videoWidth()).toBeCloseTo(900, 0);
    expect((component as any).videoHeight()).toBeCloseTo(540, 0);
  });

  it('should update selectedLesson when a lesson item in the sidebar is clicked', async () => {
    const { fixture, component } = await buildComponent(mockCourse);
    const listItems = fixture.nativeElement.querySelectorAll('mat-list-item');
    if (listItems.length > 1) {
      listItems[1].click();
      expect((component as any).selectedLesson()).toEqual(mockLesson2);
    }
  });
});
