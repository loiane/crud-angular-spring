import { TestBed } from '@angular/core/testing';

import { FormUtils } from './form-utils';

describe('FormUtils', () => {
  let service: FormUtils;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormUtils);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
