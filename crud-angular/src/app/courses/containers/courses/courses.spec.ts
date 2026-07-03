import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Courses } from './courses';

describe('Courses', () => {
  let component: Courses;
  let fixture: ComponentFixture<Courses>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Courses],
    }).compileComponents();

    fixture = TestBed.createComponent(Courses);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
