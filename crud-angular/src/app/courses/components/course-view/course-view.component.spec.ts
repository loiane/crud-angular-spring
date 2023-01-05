/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { CourseViewComponent } from './course-view.component';

describe('CourseViewComponent', () => {
  let component: CourseViewComponent;
  let fixture: ComponentFixture<CourseViewComponent>;
  let activatedRouteSpy: jasmine.SpyObj<ActivatedRoute>;

  beforeEach(async(() => {
    activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', ['']);
    TestBed.configureTestingModule({
      declarations: [CourseViewComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteSpy }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
