import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
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
export class CourseView {
  private route = inject(ActivatedRoute);
  private readonly courseData: Course = this.route.snapshot.data['course'];

  protected course = signal<Course>(this.courseData);
  protected selectedLesson = signal<Lesson | null>(
    this.courseData.lessons?.[0] ?? null
  );
  protected videoHeight = signal(0);
  protected videoWidth = signal(0);
  protected youTubePlayer = viewChild<ElementRef<HTMLDivElement>>('youTubePlayer');

  constructor() {
    afterNextRender(() => this.onResize());
    fromEvent(window, 'resize')
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.onResize());
  }

  protected onResize(): void {
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
