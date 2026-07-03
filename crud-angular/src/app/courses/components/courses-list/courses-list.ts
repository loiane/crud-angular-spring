import { Component, ChangeDetectionStrategy, input, output } from '@angular/core';

import { Course } from '../../model/course';
import { CategoryPipe } from '../../../shared/pipes/category-pipe';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-courses-list',
  templateUrl: './courses-list.html',
  styleUrl: './courses-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatTableModule, MatIconModule, MatButtonModule, CategoryPipe]
})
export class CoursesList {
  courses = input<Course[]>([]);
  edit = output<Course>();
  remove = output<Course>();
  add = output<boolean>();
  view = output<Course>();

  protected readonly displayedColumns = ['name', 'category', 'actions'];

  protected onAdd() {
    this.add.emit(true);
  }

  protected onEdit(record: Course) {
    this.edit.emit(record);
  }

  protected onRemove(record: Course) {
    this.remove.emit(record);
  }

  protected onView(record: Course) {
    this.view.emit(record);
  }
}
