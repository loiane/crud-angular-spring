import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  NO_ERRORS_SCHEMA,
  OnInit,
  ViewChild
} from '@angular/core';
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
export class CourseViewComponent implements OnInit, AfterViewInit {
  course!: Course;
  selectedLesson!: Lesson;
  videoHeight!: number;
  videoWidth!: number;

  @ViewChild('youTubePlayer') youTubePlayer!: ElementRef<HTMLDivElement>;

  constructor(
    private route: ActivatedRoute,
    private changeDetectorRef: ChangeDetectorRef) { }

  ngOnInit() {
    this.course = this.route.snapshot.data['course'];
    if (this.course.lessons) this.selectedLesson = this.course.lessons[0];

    // This code loads the IFrame Player API code asynchronously, according to the instructions at
    // https://developers.google.com/youtube/iframe_api_reference#Getting_Started
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  ngAfterViewInit(): void {
    this.onResize();
    window.addEventListener('resize', this.onResize.bind(this));
  }

  onResize(): void {
    this.videoWidth = this.youTubePlayer.nativeElement.clientWidth * 0.9;
    this.videoHeight = this.videoWidth * 0.6;
    this.changeDetectorRef.detectChanges();
  }

  display(lesson: Lesson) {
    this.selectedLesson = lesson;
  }

  displaySelectedLesson(lesson: Lesson) {
    return this.selectedLesson === lesson;
  }
}
