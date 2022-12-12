import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { AppMaterialModule } from '../shared/app-material/app-material.module';
import { CoursesListComponent } from './components/courses-list/courses-list.component';
import { CoursesComponent } from './containers/courses/courses.component';
import { CoursesRoutingModule } from './courses-routing.module';

@NgModule({
  declarations: [CoursesComponent, CoursesListComponent],
  imports: [CommonModule, CoursesRoutingModule, AppMaterialModule]
})
export class CoursesModule { }
