import { Component, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-error-dialog',
  imports: [MatDialogModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h1 mat-dialog-title style="color: red;">Error!</h1>
    <div mat-dialog-content>{{ data }}</div>
    <div mat-dialog-actions align="center">
      <button mat-stroked-button mat-dialog-close>Close</button>
    </div>
  `
})
export class ErrorDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: string) { }
}
