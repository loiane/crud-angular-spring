import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
    selector: 'app-root',
    template: `
    <mat-toolbar color="primary">
      <h1 [routerLink]="['/']" style="cursor: pointer;">CRUD Angular</h1>
    </mat-toolbar>
    <router-outlet></router-outlet>
  `,
    standalone: true,
    imports: [MatToolbarModule, RouterLink, RouterOutlet]
})
export class AppComponent { }
