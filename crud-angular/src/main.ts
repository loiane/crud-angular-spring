import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { bootstrapApplication, BrowserModule } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { PreloadAllModules, provideRouter, withPreloading } from '@angular/router';

import { AppComponent } from './app/app.component';
import { APP_ROUTES } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(BrowserModule, MatToolbarModule),
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()),
    provideRouter(APP_ROUTES, withPreloading(PreloadAllModules)) //, withDebugTracing())
  ]
}).catch(err => console.error(err));
