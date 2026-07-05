import { Pipe, PipeTransform } from '@angular/core';

/**
 * Maps a course category to the Material icon that represents it.
 */
@Pipe({
  name: 'categoryIcon',
})
export class CategoryIconPipe implements PipeTransform {
  transform(value: string): string {
    switch (value?.toLowerCase()) {
      case 'front-end':
        return 'code';
      case 'back-end':
        return 'computer';
    }
    return 'code';
  }
}
