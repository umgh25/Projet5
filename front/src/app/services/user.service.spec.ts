import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';
import { userMock, userPath } from 'src/mocks/user.mocks';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve user by id', () => {
    const response: User = userMock;
    service.getById('1').subscribe((user) => {
      expect(user).toEqual(response);
    });
    const req = httpMock.expectOne(`${userPath}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should delete user by id', () => {
    service.delete('1').subscribe((response) => {
      expect(response).toBeTruthy();
    });
    const req = httpMock.expectOne(`${userPath}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it("should handle error when id to retrieve doesn't exist", () => {
    service.getById('1').subscribe({
      next: () => fail('expected an error, not a user'),
      error: (error) => expect(error.status).toBe(404),
    });

    const req = httpMock.expectOne(`${userPath}/1`);
    expect(req.request.method).toBe('GET');
    req.flush('Server error', { status: 404, statusText: 'Not found' });
  });

  it("should handle error when id to delete doesn't exist", () => {
    service.delete('1').subscribe({
      next: () => fail('expected an error, not a confirmation'),
      error: (error) => expect(error.status).toBe(404),
    });

    const req = httpMock.expectOne(`${userPath}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush('Server error', { status: 404, statusText: 'Not found' });
  });
});