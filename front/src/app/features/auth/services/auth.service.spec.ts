import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { expect } from '@jest/globals';
import {
  authPath,
  loginRequestMock,
  sessionInformation,
  registerRequestMock,
  invalidLoginRequestMock,
  invalidRegisterRequestMock,
} from 'src/mocks/auth.mocks';

describe('AuthService', () => {
  let authService: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  it('should have methods', () => {
    expect(authService.register).toBeInstanceOf(Function);
    expect(authService.login).toBeInstanceOf(Function);
  });

  describe('register', () => {
    it('should make a POST request for register', () => {
      authService.register(registerRequestMock).subscribe();

      const req = httpMock.expectOne(`${authPath}/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerRequestMock);
      req.flush({});
    });

    it('should handle HTTP errors for register', () => {
      authService.register(invalidRegisterRequestMock).subscribe({
        next: () => {},
        error: (error) => {
          expect(error).toBeTruthy();
        },
      });

      const req = httpMock.expectOne(`${authPath}/register`);
      req.flush("Bad request", { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('login', () => {
    it('should make a POST request for login and return session info', () => {
      authService.login(loginRequestMock).subscribe((sessionInfo) => {
        expect(sessionInfo).toEqual(sessionInformation);
      });

      const req = httpMock.expectOne(`${authPath}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequestMock);
      req.flush(sessionInformation);
    });

    it('should handle HTTP errors for login', () => {
      authService.login(invalidLoginRequestMock).subscribe({
        next: () => {},
        error: (error) => {
          expect(error).toBeTruthy();
        },
      });

      const req = httpMock.expectOne(`${authPath}/login`);
      req.flush("Bad credentials", { status: 401, statusText: 'Unauthorized' });
    });
  });
});