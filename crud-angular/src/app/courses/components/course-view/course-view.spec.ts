import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseView } from './course-view';

describe('CourseView', () => {
  let component: CourseView;
  let fixture: ComponentFixture<CourseView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseView],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
