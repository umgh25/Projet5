import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { SessionService } from '../services/session.service';
import { expect } from '@jest/globals';
import { createRouterMock, loginResponseMock  } from '../mocks/auth.mocks';
import { createSessionServiceMock } from '../mocks/session.mocks';


describe('AuthGuard', () => {
  let guard: AuthGuard;
  let router: jest.Mocked<Router>;
  let sessionService: jest.Mocked<SessionService>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: createRouterMock() },
        { provide: SessionService, useValue: createSessionServiceMock() },
      ],
    });

    guard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true and not navigate when user is logged in', () => {
    sessionService.isLogged = true;
    const result = guard.canActivate();
    expect(result).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should return false and navigate to login when user is not logged in', () => {
    sessionService.isLogged = false;
    const result = guard.canActivate();
    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['login']);
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });

  it('should handle edge cases when sessionInformation is undefined but isLogged is true', () => {
    sessionService.isLogged = true;
    sessionService.sessionInformation = undefined;
    const result = guard.canActivate();
    expect(result).toBe(true);
  });

  it('should properly handle state changes through SessionService', () => {
    // Test initial state
    expect(guard.canActivate()).toBe(false);

    // Simulate user login
    sessionService.isLogged = true;
    sessionService.sessionInformation = {
      ...loginResponseMock,
      admin: false,
    };

    expect(guard.canActivate()).toBe(true);

    // Simulate logout
    sessionService.isLogged = false;
    sessionService.sessionInformation = undefined;
    expect(guard.canActivate()).toBe(false);
  });
});