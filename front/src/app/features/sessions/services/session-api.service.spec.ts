import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { Session } from '../interfaces/session.interface';
import { sessionApiPath, sessionsMock } from 'src/mocks/session-api.mocks';


describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all()', () => {
    it('should retrieve all sessions', () => {
      const response: Session[] = sessionsMock;
      service.all().subscribe((sessions) => {
        expect(sessions).toEqual(response);
      });
      const req = httpMock.expectOne(`${sessionApiPath}`);
      expect(req.request.method).toBe('GET');
      req.flush(response);
    });

    it('should handle error when retrieving all sessions', () => {
      service.all().subscribe({
        next: () => fail('expected an error, not sessions'),
        error: (error) => expect(error.status).toBe(500),
      });

      const req = httpMock.expectOne(`${sessionApiPath}`);
      expect(req.request.method).toBe('GET');
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });

    it('should handle empty response when retrieving all sessions', () => {
      service.all().subscribe((sessions) => {
        expect(sessions).toEqual([]);
      });

      const req = httpMock.expectOne(`${sessionApiPath}`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });

  describe('detail()', () => {
    it('should retrieve session details by ID', () => {
      const response: Session = sessionsMock[0];
      service.detail('1').subscribe((session) => {
        expect(session).toEqual(response);
      });
      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(response);
    });

    it('should handle error when retrieving session details by ID', () => {
      service.detail('1').subscribe({
        next: () => fail('expected an error, not a session'),
        error: (error) => expect(error.status).toBe(404),
      });

      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('GET');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle null response when retrieving session details by ID', () => {
      service.detail('1').subscribe((session) => {
        expect(session).toBeNull();
      });

      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(null);
    });
  });

  describe('delete()', () => {
    it('should delete a session by ID', () => {
      service.delete('1').subscribe((response) => {
        expect(response).toBeTruthy();
      });
      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });

    it('should handle error when deleting a session by ID', () => {
      service.delete('1').subscribe({
        next: () => fail('expected an error, not a confirmation'),
        error: (error) => expect(error.status).toBe(500),
      });
      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('create()', () => {
    it('should create a new session', () => {
      const newSession: Session = sessionsMock[0];
      service.create(newSession).subscribe((session) => {
        expect(session).toEqual(newSession);
      });
      const req = httpMock.expectOne(`${sessionApiPath}`);
      expect(req.request.method).toBe('POST');
      req.flush(newSession);
    });

    it('should handle error when creating a new session', () => {
      const newSession: Session = sessionsMock[0];
      service.create(newSession).subscribe({
        next: () => fail('expected an error, not a session'),
        error: (error) => expect(error.status).toBe(400),
      });
      const req = httpMock.expectOne(`${sessionApiPath}`);
      expect(req.request.method).toBe('POST');
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('update()', () => {
    it('should update an existing session', () => {
      const updatedSession: Session = sessionsMock[0];
      service.update('1', updatedSession).subscribe((session) => {
        expect(session).toEqual(updatedSession);
      });
      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('PUT');
      req.flush(updatedSession);
    });

    it('should handle error when updating an existing session', () => {
      const updatedSession: Session = sessionsMock[0];
      service.update('1', updatedSession).subscribe({
        next: () => fail('expected an error, not a session'),
        error: (error) => expect(error.status).toBe(404),
      });
      const req = httpMock.expectOne(`${sessionApiPath}/1`);
      expect(req.request.method).toBe('PUT');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('participate()', () => {
    it('should add a user to a session', () => {
      service.participate('1', '1').subscribe((response) => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${sessionApiPath}/1/participate/1`);
      expect(req.request.method).toBe('POST');
      req.flush({});
    });

    it('should handle error when adding a user to a session', () => {
      service.participate('1', '1').subscribe({
        next: () => fail('expected an error, not a response'),
        error: (error) => expect(error.status).toBe(500),
      });
      const req = httpMock.expectOne(`${sessionApiPath}/1/participate/1`);
      expect(req.request.method).toBe('POST');
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });

    it('should remove a user from a session', () => {
      service.unParticipate('1', '1').subscribe((response) => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${sessionApiPath}/1/participate/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });

    it('should handle error when removing a user from a session', () => {
      service.unParticipate('1', '1').subscribe({
        next: () => fail('expected an error, not a response'),
        error: (error) => expect(error.status).toBe(500),
      });

      const req = httpMock.expectOne(`${sessionApiPath}/1/participate/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });
  });
});