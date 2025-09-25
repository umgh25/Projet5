import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';
import { getAllTeachersResponseMock, teacherPath } from 'src/mocks/teacher.mocks';


describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService],
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all teachers', () => {
    const response: Teacher[] = getAllTeachersResponseMock;
    service.all().subscribe((teachers) => {
      expect(teachers).toEqual(response);
    });

    const req = httpMock.expectOne(teacherPath);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should fetch teacher detail by id', () => {
    const response: Teacher = getAllTeachersResponseMock[0];
    service.detail('1').subscribe((teacher) => {
      expect(teacher).toEqual(response);
    });
    const req = httpMock.expectOne(`${teacherPath}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should handle error when fetching all teachers', () => {
    service.all().subscribe({
      next: () => fail('expected an error, not teachers'),
      error: (error) => expect(error.status).toBe(500),
    });

    const req = httpMock.expectOne(teacherPath);
    expect(req.request.method).toBe('GET');
    req.flush('Something went wrong', {
      status: 500,
      statusText: 'Server Error',
    });
  });

  it('should handle error when fetching teacher detail by id', () => {
    service.detail('1').subscribe({
      next: () => fail('expected an error, not a teacher'),
      error: (error) => expect(error.status).toBe(404),
    });

    const req = httpMock.expectOne(`${teacherPath}/1`);
    expect(req.request.method).toBe('GET');
    req.flush('Not found', { status: 404, statusText: 'Not Found' });
  });

  it('should handle empty response when fetching all teachers', () => {
    service.all().subscribe((teachers) => {
      expect(teachers).toEqual([]);
    });

    const req = httpMock.expectOne(teacherPath);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should handle null response when fetching teacher detail by id', () => {
    service.detail('1').subscribe((teacher) => {
      expect(teacher).toBeNull();
    });

    const req = httpMock.expectOne(`${teacherPath}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(null);
  });
});