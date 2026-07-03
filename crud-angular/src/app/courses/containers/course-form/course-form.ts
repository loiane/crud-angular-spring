import { Location } from '@angular/common';
import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
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
import { ActivatedRoute } from '@angular/router';
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
  private service = inject(CoursesService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private location = inject(Location);
  private route = inject(ActivatedRoute);

  private model = signal<CourseModel>(
    this.toModel(this.route.snapshot.data['course'])
  );

  protected courseForm = form(this.model, path => {
    required(path.name, { message: REQUIRED_MESSAGE });
    minLength(path.name, 5, { message: minLengthMessage(5) });
    maxLength(path.name, 100, { message: maxLengthMessage(100) });
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
        : [{ _id: '', name: '', youtubeUrl: '' }]
    };
  }

  protected addLesson() {
    this.model.update(course => ({
      ...course,
      lessons: [...course.lessons, { _id: '', name: '', youtubeUrl: '' }]
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
      } catch {
        this.onError();
      }
      return undefined;
    });
  }

  protected onCancel() {
    this.location.back();
  }

  private onSuccess() {
    this.snackBar.open('Course saved successfully!', '', { duration: 5000 });
    this.onCancel();
  }

  private onError() {
    this.dialog.open(ErrorDialog, {
      data: 'Error saving course.'
    });
  }
}
