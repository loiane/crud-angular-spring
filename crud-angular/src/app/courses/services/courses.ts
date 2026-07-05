import { HttpClient, HttpResourceRef, httpResource } from '@angular/common/http';
import { Service, Signal, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Course } from '../model/course';
import { CoursePage } from '../model/course-page';

@Service()
export class CoursesService {
  private readonly API = '/api/courses';
  private http = inject(HttpClient);

  list(page: Signal<number>, pageSize: Signal<number>): HttpResourceRef<CoursePage | undefined> {
    return httpResource<CoursePage>(() => ({
      url: this.API,
      params: { page: page(), pageSize: pageSize() }
    }));
  }

  loadById(id: string): Observable<Course> {
    return this.http.get<Course>(`${this.API}/${id}`);
  }

  save(record: Partial<Course>): Observable<Course> {
    if (record._id) {
      return this.update(record);
    }
    return this.create(record);
  }

  private update(record: Partial<Course>): Observable<Course> {
    return this.http.put<Course>(`${this.API}/${record._id}`, record);
  }

  private create(record: Partial<Course>): Observable<Course> {
    return this.http.post<Course>(this.API, record);
  }

  remove(id: string): Observable<Course> {
    return this.http.delete<Course>(`${this.API}/${id}`);
  }
}
