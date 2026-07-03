import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirmation-dialog',
  imports: [MatDialogModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div mat-dialog-content>
      <p>{{ data }}</p>
    </div>
    <div mat-dialog-actions align="center">
      <button matButton="filled" (click)="onConfirm(true)" id="yesBtn">
        Yes
      </button>
      <button matButton="outlined" (click)="onConfirm(false)" id="noBtn">
        No
      </button>
    </div>
  `
})
export class ConfirmationDialog {
  private dialogRef = inject(MatDialogRef<ConfirmationDialog>);
  protected data = inject<string>(MAT_DIALOG_DATA);

  protected onConfirm(result: boolean): void {
    this.dialogRef.close(result);
  }
}
