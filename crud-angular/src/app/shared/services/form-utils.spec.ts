import { TestBed } from '@angular/core/testing';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { FormUtilsService } from './form-utils';

describe('FormUtilsService', () => {
  let service: FormUtilsService;
  let fb: FormBuilder;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormUtilsService);
    fb = new FormBuilder();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('validateAllFormFields', () => {
    it('should mark a FormControl as touched', () => {
      const form = fb.group({ name: ['', Validators.required] });
      service.validateAllFormFields(form);
      expect(form.get('name')?.touched).toBe(true);
    });

    it('should recurse into nested FormGroup', () => {
      const form = fb.group({ nested: fb.group({ field: [''] }) });
      service.validateAllFormFields(form);
      expect(form.get('nested.field')?.touched).toBe(true);
    });

    it('should recurse into FormArray', () => {
      const form = fb.group({ items: fb.array([fb.control('')]) });
      service.validateAllFormFields(form);
      const arr = form.get('items') as any;
      expect(arr.controls[0].touched).toBe(true);
    });
  });

  describe('getErrorMessageFromField', () => {
    it('should return "Field is required." for required error', () => {
      const ctrl = new FormControl('', Validators.required) as any;
      expect(service.getErrorMessageFromField(ctrl)).toBe('Field is required.');
    });

    it('should return maxlength message with correct length', () => {
      const ctrl = new FormControl('a'.repeat(11), Validators.maxLength(10)) as any;
      expect(service.getErrorMessageFromField(ctrl)).toContain('more than 10');
    });

    it('should return minlength message with correct length', () => {
      const ctrl = new FormControl('ab', Validators.minLength(5)) as any;
      expect(service.getErrorMessageFromField(ctrl)).toContain('less than 5');
    });

    it('should return empty string for valid field', () => {
      const ctrl = new FormControl('valid value') as any;
      expect(service.getErrorMessageFromField(ctrl)).toBe('');
    });
  });

  describe('getFieldErrorMessage', () => {
    it('should delegate to getErrorMessageFromField', () => {
      const form = fb.group({ name: ['', Validators.required] });
      expect(service.getFieldErrorMessage(form, 'name')).toBe('Field is required.');
    });
  });

  describe('getFieldFormArrayErrorMessage', () => {
    it('should return error message for a field inside a FormArray item', () => {
      const form = fb.group({
        lessons: fb.array([fb.group({ name: ['', Validators.required] })])
      });
      const msg = service.getFieldFormArrayErrorMessage(form, 'lessons', 'name', 0);
      expect(msg).toBe('Field is required.');
    });
  });

  describe('isFormArrayRequired', () => {
    it('should return true when empty FormArray is required and touched', () => {
      const form = fb.group({ lessons: fb.array([], Validators.required) }) as any;
      form.get('lessons')?.markAsTouched();
      expect(service.isFormArrayRequired(form, 'lessons')).toBe(true);
    });

    it('should return false when FormArray has items', () => {
      const form = fb.group({ lessons: fb.array([fb.control('')], Validators.required) }) as any;
      form.get('lessons')?.markAsTouched();
      expect(service.isFormArrayRequired(form, 'lessons')).toBe(false);
    });

    it('should return false when FormArray is not touched', () => {
      const form = fb.group({ lessons: fb.array([], Validators.required) }) as any;
      expect(service.isFormArrayRequired(form, 'lessons')).toBe(false);
    });
  });
});
