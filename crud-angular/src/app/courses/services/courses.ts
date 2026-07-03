import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';

import { Course } from '../model/course';

@Service()
export class CoursesService {
  private readonly API = '/api/courses';
  private http = inject(HttpClient);

  loadById(id: string) {
    return this.http.get<Course>(`${this.API}/${id}`);
  }

  save(record: Partial<Course>) {
    if (record._id) {
      return this.update(record);
    }
    return this.create(record);
  }

  private update(record: Partial<Course>) {
    return this.http.put<Course>(`${this.API}/${record._id}`, record);
  }

  private create(record: Partial<Course>) {
    return this.http.post<Course>(this.API, record);
  }

  remove(id: string) {
    return this.http.delete<Course>(`${this.API}/${id}`);
  }
}
