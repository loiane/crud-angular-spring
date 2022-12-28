import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesComponent } from './courses.component';

describe('CoursesComponent', () => {
  let component: CoursesComponent;
  let fixture: ComponentFixture<CoursesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CoursesComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    //fixture = TestBed.createComponent(CoursesComponent);
    //component = fixture.componentInstance;
    //fixture.detectChanges();
  });

  it('should create', () => {
    pending();
  });
});
