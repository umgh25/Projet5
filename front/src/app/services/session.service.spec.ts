import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { first } from 'rxjs';
import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { adminRequestMock } from 'src/mocks/session.mocks';


describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in a user', () => {
    const user: SessionInformation = adminRequestMock;
    service.logIn(user);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(user);
  });

  it('should log out a user', () => {
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit isLogged as true when user logs in', (done) => {
    const user: SessionInformation = adminRequestMock;

    service.$isLogged().subscribe((isLogged) => {
      if (isLogged) {
        expect(isLogged).toBe(true);
        done();
      }
    });

    service.logIn(user);
  });

  it('should emit isLogged as false when user logs out', (done) => {
    service
      .$isLogged()
      .pipe(first())
      .subscribe((isLogged) => {
        expect(isLogged).toBe(false);
        done();
      });

    service.logOut();
  });
});