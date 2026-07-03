import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseForm } from './course-form';

describe('CourseForm', () => {
  let component: CourseForm;
  let fixture: ComponentFixture<CourseForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseForm],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
