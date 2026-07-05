import { Location } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, ChangeDetectionStrategy, inject, input, linkedSignal } from '@angular/core';
import {
  FormField,
  applyEach,
  form,
  maxLength,
  minLength,
  required,
  submit
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { firstValueFrom } from 'rxjs';

import { Course } from '../../model/course';
import { Lesson } from '../../model/lesson';
import { CoursesService } from '../../services/courses';
import { ErrorDialog } from '../../../shared/components/error-dialog/error-dialog';

interface CourseModel {
  _id: string;
  name: string;
  category: string;
  lessons: Lesson[];
}

const NAME_MAX_LENGTH = 150;

const REQUIRED_MESSAGE = 'Field is required.';
const minLengthMessage = (length: number) =>
  `Field cannot be less than ${length} characters long.`;
const maxLengthMessage = (length: number) =>
  `Field cannot be more than ${length} characters long.`;

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.html',
  styleUrl: './course-form.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    FormField,
    MatCardModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule
  ]
})
export class CourseForm {
  course = input.required<Course>();

  protected readonly nameMaxLength = NAME_MAX_LENGTH;

  private service = inject(CoursesService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private location = inject(Location);

  private model = linkedSignal<CourseModel>(() => this.toModel(this.course()));

  protected courseForm = form(this.model, path => {
    required(path.name, { message: REQUIRED_MESSAGE });
    minLength(path.name, 5, { message: minLengthMessage(5) });
    maxLength(path.name, NAME_MAX_LENGTH, { message: maxLengthMessage(NAME_MAX_LENGTH) });
    required(path.category, { message: REQUIRED_MESSAGE });
    minLength(path.lessons, 1, { message: 'At least one lesson is required.' });
    applyEach(path.lessons, lesson => {
      required(lesson.name, { message: REQUIRED_MESSAGE });
      minLength(lesson.name, 5, { message: minLengthMessage(5) });
      maxLength(lesson.name, 100, { message: maxLengthMessage(100) });
      required(lesson.youtubeUrl, { message: REQUIRED_MESSAGE });
      minLength(lesson.youtubeUrl, 10, { message: minLengthMessage(10) });
      maxLength(lesson.youtubeUrl, 11, { message: maxLengthMessage(11) });
    });
  });

  private toModel(course: Course): CourseModel {
    return {
      _id: course._id ?? '',
      name: course.name ?? '',
      category: course.category ?? '',
      lessons: course.lessons?.length
        ? course.lessons
        : [{ _id: 0, name: '', youtubeUrl: '' }]
    };
  }

  protected addLesson() {
    this.model.update(course => ({
      ...course,
      lessons: [...course.lessons, { _id: 0, name: '', youtubeUrl: '' }]
    }));
  }

  protected removeLesson(index: number) {
    this.model.update(course => ({
      ...course,
      lessons: course.lessons.filter((_, i) => i !== index)
    }));
  }

  protected onSubmit() {
    return submit(this.courseForm, async () => {
      try {
        await firstValueFrom(this.service.save(this.model()));
        this.onSuccess();
      } catch (err) {
        this.onError(this.extractErrorMessage(err));
      }
      return undefined;
    });
  }

  /**
   * Surfaces the RFC 7807 detail returned by the API (e.g. a duplicate
   * course name), falling back to a generic message.
   */
  private extractErrorMessage(err: unknown): string {
    const detail = (err as HttpErrorResponse)?.error?.detail;
    return typeof detail === 'string' && detail ? detail : 'Error saving course.';
  }

  protected onCancel() {
    this.location.back();
  }

  private onSuccess() {
    this.snackBar.open('Course saved successfully!', '', { duration: 5000 });
    this.onCancel();
  }

  private onError(message: string) {
    this.dialog.open(ErrorDialog, {
      data: message
    });
  }
}
