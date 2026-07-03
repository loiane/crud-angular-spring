import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CoursesList } from './courses-list';
import { Course } from '../../model/course';

const mockCourses: Course[] = [
  { _id: '1', name: 'Angular Basics', category: 'front-end' },
  { _id: '2', name: 'Spring Boot', category: 'back-end' }
];

describe('CoursesList', () => {
  let component: CoursesList;
  let fixture: ComponentFixture<CoursesList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursesList]
    }).compileComponents();

    fixture = TestBed.createComponent(CoursesList);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('courses', mockCourses);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render correct number of rows', () => {
    const rows = fixture.nativeElement.querySelectorAll('mat-row');
    expect(rows.length).toBe(2);
  });

  it('should emit true on add output when onAdd() is called', () => {
    let emitted: boolean | undefined;
    component.add.subscribe(v => (emitted = v));
    (component as any).onAdd();
    expect(emitted).toBe(true);
  });

  it('should emit course on edit output when onEdit() is called', () => {
    let emitted: Course | undefined;
    component.edit.subscribe(v => (emitted = v));
    (component as any).onEdit(mockCourses[0]);
    expect(emitted).toEqual(mockCourses[0]);
  });

  it('should emit course on remove output when onRemove() is called', () => {
    let emitted: Course | undefined;
    component.remove.subscribe(v => (emitted = v));
    (component as any).onRemove(mockCourses[0]);
    expect(emitted).toEqual(mockCourses[0]);
  });

  it('should emit course on view output when onView() is called', () => {
    let emitted: Course | undefined;
    component.view.subscribe(v => (emitted = v));
    (component as any).onView(mockCourses[1]);
    expect(emitted).toEqual(mockCourses[1]);
  });

  it('should emit view when the course name link is clicked', () => {
    let emitted: Course | undefined;
    component.view.subscribe(v => (emitted = v));
    const nameLink = fixture.nativeElement.querySelector('mat-cell a');
    nameLink?.click();
    expect(emitted).toEqual(mockCourses[0]);
  });

  it('should emit add when the Add button in the header is clicked', () => {
    let emitted: boolean | undefined;
    component.add.subscribe(v => (emitted = v));
    const addBtn = fixture.nativeElement.querySelector('button[aria-label="Add a new Course"]');
    addBtn?.click();
    expect(emitted).toBe(true);
  });

  it('should emit edit when the Edit button in a row is clicked', () => {
    let emitted: Course | undefined;
    component.edit.subscribe(v => (emitted = v));
    const editBtn = fixture.nativeElement.querySelector('button[aria-label="Update Course"]');
    editBtn?.click();
    expect(emitted).toEqual(mockCourses[0]);
  });

  it('should emit remove when the Remove button in a row is clicked', () => {
    let emitted: Course | undefined;
    component.remove.subscribe(v => (emitted = v));
    const removeBtn = fixture.nativeElement.querySelector('button[aria-label="Remove Course"]');
    removeBtn?.click();
    expect(emitted).toEqual(mockCourses[0]);
  });
});
