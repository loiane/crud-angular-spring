import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';

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
  @Input() courses: Course[] = [];
  @Output() details: EventEmitter<Course> = new EventEmitter(false);
  @Output() edit: EventEmitter<Course> = new EventEmitter(false);
  @Output() remove: EventEmitter<Course> = new EventEmitter(false);
  @Output() add: EventEmitter<boolean> = new EventEmitter(false);
  @Output() view: EventEmitter<Course> = new EventEmitter(false);

  readonly displayedColumns = ['name', 'category', 'actions'];

  onDetails(record: Course) {
    this.details.emit(record);
  }

  onAdd() {
    this.add.emit(true);
  }

  onEdit(record: Course) {
    this.edit.emit(record);
  }

  onRemove(record: Course) {
    this.remove.emit(record);
  }

  onView(record: Course) {
    this.view.emit(record);
  }
}
