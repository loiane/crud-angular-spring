import { CategoryPipe } from './category.pipe';

describe('Pipe: Category', () => {
  it('create an instance', () => {
    const pipe = new CategoryPipe();
    expect(pipe).toBeTruthy();
  });

  it('transforms a category to an icon', () => {
    const pipe = new CategoryPipe();
    expect(pipe.transform('front-end')).toBe('code');
    expect(pipe.transform('back-end')).toBe('computer');
    expect(pipe.transform('')).toBe('code');
  });
});
