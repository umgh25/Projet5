import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../services/session.service';
import { expect } from '@jest/globals';
import { createRouterMock } from '../mocks/auth.mocks';
import { createSessionServiceMock } from '../mocks/session.mocks';


describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let router: jest.Mocked<Router>;
  let sessionService: jest.Mocked<SessionService>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: Router, useValue: createRouterMock() },
        { provide: SessionService, useValue: createSessionServiceMock() },
      ],
    });

    guard = TestBed.inject(UnauthGuard);
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
  });

  it('should create the guard', () => {
    expect(guard).toBeTruthy();
  });

  it('should implement CanActivate', () => {
    expect(typeof guard.canActivate).toBe('function');
  });

  it('should return false and navigate to rentals when user is logged in', () => {
    sessionService.isLogged = true;
    const result = guard.canActivate();
    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['rentals']);
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });

  it('should return true and not navigate when user is not logged in', () => {
    sessionService.isLogged = false;
    const result = guard.canActivate();
    expect(result).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});