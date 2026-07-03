import { Location } from '@angular/common';
import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import {
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  UntypedFormArray,
  Validators
} from '@angular/forms';
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
import { FormUtilsService } from '../../../shared/services/form-utils';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.html',
  styleUrl: './course-form.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatCardModule,
    MatToolbarModule,
    ReactiveFormsModule,
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
  protected form: FormGroup;
  protected formUtils = inject(FormUtilsService);

  private formBuilder = inject(NonNullableFormBuilder);
  private service = inject(CoursesService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private location = inject(Location);
  private route = inject(ActivatedRoute);

  constructor() {
    const course: Course = this.route.snapshot.data['course'];
    this.form = this.formBuilder.group({
      _id: [course._id],
      name: [
        course.name,
        [Validators.required, Validators.minLength(5), Validators.maxLength(100)]
      ],
      category: [course.category, [Validators.required]],
      lessons: this.formBuilder.array(this.retrieveLessons(course), Validators.required)
    });
  }

  private retrieveLessons(course: Course) {
    const lessons = [];
    if (course?.lessons) {
      course.lessons.forEach(lesson => lessons.push(this.createLesson(lesson)));
    } else {
      lessons.push(this.createLesson());
    }
    return lessons;
  }

  private createLesson(lesson: Lesson = { _id: '', name: '', youtubeUrl: '' }) {
    return this.formBuilder.group({
      _id: [lesson._id],
      name: [
        lesson.name,
        [Validators.required, Validators.minLength(5), Validators.maxLength(100)]
      ],
      youtubeUrl: [
        lesson.youtubeUrl,
        [Validators.required, Validators.minLength(10), Validators.maxLength(11)]
      ]
    });
  }

  protected getLessonFormArray() {
    return (<UntypedFormArray>this.form.get('lessons')).controls;
  }

  protected getErrorMessage(fieldName: string): string {
    return this.formUtils.getFieldErrorMessage(this.form, fieldName);
  }

  protected getLessonErrorMessage(fieldName: string, index: number) {
    return this.formUtils.getFieldFormArrayErrorMessage(
      this.form,
      'lessons',
      fieldName,
      index
    );
  }

  protected addLesson(): void {
    const lessons = this.form.get('lessons') as UntypedFormArray;
    lessons.push(this.createLesson());
  }

  protected removeLesson(index: number) {
    const lessons = this.form.get('lessons') as UntypedFormArray;
    lessons.removeAt(index);
  }

  protected async onSubmit() {
    if (this.form.valid) {
      try {
        await firstValueFrom(this.service.save(this.form.value as Course));
        this.onSuccess();
      } catch {
        this.onError();
      }
    } else {
      this.formUtils.validateAllFormFields(this.form);
    }
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
