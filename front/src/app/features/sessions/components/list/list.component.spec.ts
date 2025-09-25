import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { adminRequestMock, createSessionServiceMock } from 'src/mocks/session.mocks';
import { SessionApiService } from '../../services/session-api.service';
import { createSessionApiServiceMock, sessionsMock } from 'src/mocks/session-api.mocks';
import { RouterModule } from '@angular/router';
import { sessionInformation } from 'src/mocks/auth.mocks';


describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionService: jest.Mocked<SessionService>;
  let sessionApiService: jest.Mocked<SessionApiService>;


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule, RouterModule.forRoot([])],
      providers: [
        { provide: SessionService, useValue: createSessionServiceMock() },
        { provide: SessionApiService, useValue: createSessionApiServiceMock() }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;

    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('Sessions Observable', () => {
    it('should retrieve sessions from the API', (done) => {
      component.sessions$.subscribe((sessions) => {
        expect(sessions).toEqual(sessionsMock);
        done();
      });
    });
  });

  describe('User Getter', () => {
    it('should return undefined when user is not logged in', () => {
      sessionService.sessionInformation = undefined;
      expect(component.user).toBeUndefined();
    });

    it('should return the user information when user is logged in', () => {
      sessionService.sessionInformation = sessionInformation;
      expect(component.user).toEqual(sessionInformation);
    });
  });
});