import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  OnInit,
  afterNextRender,
  inject,
  signal,
  viewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ActivatedRoute } from '@angular/router';
import { fromEvent } from 'rxjs';
import { YouTubePlayerModule } from '@angular/youtube-player';

import { Course } from '../../model/course';
import { Lesson } from '../../model/lesson';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.html',
  styleUrl: './course-view.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatSidenavModule,
    MatButtonModule,
    MatListModule,
    YouTubePlayerModule
  ]
})
export class CourseView implements OnInit {
  private route = inject(ActivatedRoute);

  protected course = signal<Course | null>(null);
  protected selectedLesson = signal<Lesson | null>(null);
  protected videoHeight = signal(0);
  protected videoWidth = signal(0);
  protected youTubePlayer = viewChild<ElementRef<HTMLDivElement>>('youTubePlayer');

  constructor() {
    afterNextRender(() => this.onResize());
    fromEvent(window, 'resize')
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.onResize());
  }

  ngOnInit() {
    const courseData: Course = this.route.snapshot.data['course'];
    this.course.set(courseData);
    if (courseData.lessons?.length) {
      this.selectedLesson.set(courseData.lessons[0]);
    }

    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  private onResize(): void {
    const el = this.youTubePlayer();
    if (!el) return;
    const width = el.nativeElement.clientWidth * 0.9;
    this.videoWidth.set(width);
    this.videoHeight.set(width * 0.6);
  }

  protected display(lesson: Lesson) {
    this.selectedLesson.set(lesson);
  }

  protected displaySelectedLesson(lesson: Lesson) {
    return this.selectedLesson() === lesson;
  }
}
