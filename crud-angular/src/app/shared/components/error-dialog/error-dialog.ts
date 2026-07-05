import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-error-dialog',
  imports: [MatDialogModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    h1[mat-dialog-title] {
      color: var(--mat-sys-error);
    }
  `,
  template: `
    <h1 mat-dialog-title>Error!</h1>
    <div mat-dialog-content>{{ data }}</div>
    <div mat-dialog-actions align="center">
      <button matButton="outlined" mat-dialog-close>Close</button>
    </div>
  `
})
export class ErrorDialog {
  protected data = inject<string>(MAT_DIALOG_DATA);
}
