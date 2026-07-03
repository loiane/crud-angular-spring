import { CategoryPipe } from './category-pipe';

describe('CategoryPipe', () => {
  let pipe: CategoryPipe;

  beforeEach(() => {
    pipe = new CategoryPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return "code" for "front-end"', () => {
    expect(pipe.transform('front-end')).toBe('code');
  });

  it('should return "computer" for "back-end"', () => {
    expect(pipe.transform('back-end')).toBe('computer');
  });

  it('should return "code" as default for unknown category', () => {
    expect(pipe.transform('full-stack')).toBe('code');
  });

  it('should return "code" for empty string', () => {
    expect(pipe.transform('')).toBe('code');
  });
});
