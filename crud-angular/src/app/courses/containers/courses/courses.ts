import { Component, ChangeDetectionStrategy, inject, signal, effect } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { ConfirmationDialog } from '../../../shared/components/confirmation-dialog/confirmation-dialog';
import { ErrorDialog } from '../../../shared/components/error-dialog/error-dialog';
import { CoursesList } from '../../components/courses-list/courses-list';
import { Course } from '../../model/course';
import { CoursesService } from '../../services/courses';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.html',
  styleUrl: './courses.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatToolbarModule,
    CoursesList,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatPaginatorModule
  ]
})
export class Courses {
  private coursesService = inject(CoursesService);
  private dialog = inject(MatDialog);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  protected pageIndex = signal(0);
  protected pageSize = signal(10);

  protected coursesResource = this.coursesService.list(this.pageIndex, this.pageSize);

  constructor() {
    effect(() => {
      if (this.coursesResource.error()) {
        this.onError('Error loading courses.');
      }
    });
  }

  protected onPageChange(pageEvent: PageEvent) {
    this.pageIndex.set(pageEvent.pageIndex);
    this.pageSize.set(pageEvent.pageSize);
  }

  protected onError(errorMsg: string) {
    this.dialog.open(ErrorDialog, { data: errorMsg });
  }

  protected onAdd() {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  protected onEdit(course: Course) {
    this.router.navigate(['edit', course._id], { relativeTo: this.route });
  }

  protected onView(course: Course) {
    this.router.navigate(['view', course._id], { relativeTo: this.route });
  }

  protected async onRemove(course: Course) {
    const dialogRef = this.dialog.open(ConfirmationDialog, {
      data: 'Are you sure you would like to remove this course?'
    });
    const result = await firstValueFrom(dialogRef.afterClosed());
    if (result) {
      try {
        await firstValueFrom(this.coursesService.remove(course._id));
        this.coursesResource.reload();
        this.snackBar.open('Course removed successfully!', 'X', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'center'
        });
      } catch {
        this.onError('Error trying to remove the course.');
      }
    }
  }
}
