import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesList } from './courses-list';

describe('CoursesList', () => {
  let component: CoursesList;
  let fixture: ComponentFixture<CoursesList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursesList],
    }).compileComponents();

    fixture = TestBed.createComponent(CoursesList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
