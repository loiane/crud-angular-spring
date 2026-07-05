import { CategoryIconPipe } from './category-pipe';

describe('CategoryIconPipe', () => {
  let pipe: CategoryIconPipe;

  beforeEach(() => {
    pipe = new CategoryIconPipe();
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

  it('should match categories regardless of casing', () => {
    expect(pipe.transform('Front-end')).toBe('code');
    expect(pipe.transform('Back-end')).toBe('computer');
  });

  it('should return "code" as default for unknown category', () => {
    expect(pipe.transform('full-stack')).toBe('code');
  });

  it('should return "code" for empty string', () => {
    expect(pipe.transform('')).toBe('code');
  });
});
