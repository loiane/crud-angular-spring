import { ChangeDetectionStrategy, Component, NO_ERRORS_SCHEMA, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ActivatedRoute } from '@angular/router';
import { MatListModule } from '@angular/material/list';

import { Course } from '../../model/course';
import { NgFor, NgIf } from '@angular/common';
import { Lesson } from '../../model/lesson';
import { YouTubePlayerModule } from '@angular/youtube-player';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
  styleUrls: ['./course-view.component.scss'],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    NgIf,
    NgFor,
    MatSidenavModule,
    MatButtonModule,
    MatListModule,
    YouTubePlayerModule
  ],
  schemas: [NO_ERRORS_SCHEMA]
})
export class CourseViewComponent implements OnInit {
  course!: Course;
  selectedLesson!: Lesson;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.course = this.route.snapshot.data['course'];
    if (this.course.lessons) this.selectedLesson = this.course.lessons[0];

    // This code loads the IFrame Player API code asynchronously, according to the instructions at
    // https://developers.google.com/youtube/iframe_api_reference#Getting_Started
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  display(lesson: Lesson) {
    this.selectedLesson = lesson;
  }
}
