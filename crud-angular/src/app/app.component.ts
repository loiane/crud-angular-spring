import { Component, ChangeDetectionStrategy } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-root',
    imports: [MatToolbarModule, RouterLink, RouterOutlet],
    changeDetection: ChangeDetectionStrategy.Eager,
    template: `
    <mat-toolbar color="primary">
      <h1 [routerLink]="['/']" style="cursor: pointer;">Courses App</h1>
    </mat-toolbar>
    <router-outlet></router-outlet>
  `
})
export class AppComponent { }
