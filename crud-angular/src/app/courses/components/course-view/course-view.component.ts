import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Course } from '../../model/course';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
  styleUrls: ['./course-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CourseViewComponent implements OnInit {
  course: Course | null = null;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    // this.course = this.route.snapshot.data['course'];
  }
}
