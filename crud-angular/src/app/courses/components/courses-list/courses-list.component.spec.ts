import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { coursesMock } from './../../services/courses.mock';
import { CoursesListComponent } from './courses-list.component';

describe('CoursesListComponent', () => {
  let component: CoursesListComponent;
  let fixture: ComponentFixture<CoursesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursesListComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CoursesListComponent);
    component = fixture.componentInstance;
    component.courses = coursesMock;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should check if displayedColumns have value', () => {
    expect(component.displayedColumns).toBeTruthy();
    expect(component.displayedColumns.length).toBe(3);
  });

  it('should check if courses have value', () => {
    expect(component.courses).toBeTruthy();
    expect(component.courses.length).toBe(coursesMock.length);
  });

  it('Should emit event when click on details', () => {
    spyOn(component.details, 'emit');
    const course = coursesMock[0];
    component.onDetails(course);
    expect(component.details.emit).toHaveBeenCalledWith(course);
  });

  it('Should emit event when click on edit', () => {
    spyOn(component.edit, 'emit');
    const course = coursesMock[0];
    component.onEdit(course);
    expect(component.edit.emit).toHaveBeenCalledWith(course);
  });

  it('Should emit event when click on remove', () => {
    spyOn(component.remove, 'emit');
    const course = coursesMock[0];
    component.onRemove(course);
    expect(component.remove.emit).toHaveBeenCalledWith(course);
  });

  it('Should emit event when click on add', () => {
    spyOn(component.add, 'emit');
    component.onAdd();
    expect(component.add.emit).toHaveBeenCalled();
  });
});
