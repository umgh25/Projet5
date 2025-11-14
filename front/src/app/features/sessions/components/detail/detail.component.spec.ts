import { NgZone } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';

import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';

import { adminRequestMock, createSessionServiceMock } from 'src/mocks/session.mocks';
import { createSessionApiServiceMock, sessionsMock } from 'src/mocks/session-api.mocks';
import { createTeacherServiceMock, getAllTeachersResponseMock } from 'src/mocks/teacher.mocks';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: Router;

  let sessionService: jest.Mocked<SessionService>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let teacherService: jest.Mocked<TeacherService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;
  let ngZone: NgZone;

  const sessionId = '1';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: createSessionServiceMock() },
        { provide: SessionApiService, useValue: createSessionApiServiceMock() },
        { provide: TeacherService, useValue: createTeacherServiceMock() },
        { provide: MatSnackBar, useValue: { open: jest.fn() } },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: jest.fn().mockReturnValue(sessionId) } } }
        }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    teacherService = TestBed.inject(TeacherService) as jest.Mocked<TeacherService>;
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;
    ngZone = TestBed.inject(NgZone);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expose needed methods', () => {
    expect(component.ngOnInit).toBeDefined();
    expect(component.back).toBeInstanceOf(Function);
    expect(component.delete).toBeInstanceOf(Function);
    expect(component.participate).toBeInstanceOf(Function);
    expect(component.unParticipate).toBeInstanceOf(Function);
  });

  it('should initialize sessionId, userId and isAdmin', () => {
    expect(component.sessionId).toBe(sessionId);
    expect(component.userId).toBe(adminRequestMock.id.toString());
    expect(component.isAdmin).toBe(adminRequestMock.admin);
  });

  // ----------------------------------------
  // ngOnInit / fetchSession
  // ----------------------------------------
  it('should call services inside fetchSession() when ngOnInit runs', () => {
    const sessionSpy = jest.spyOn(sessionApiService, 'detail');
    const teacherSpy = jest.spyOn(teacherService, 'detail');

    component.ngOnInit();

    expect(sessionSpy).toHaveBeenCalledWith(sessionId);

    const firstSession = sessionsMock[parseInt(sessionId) - 1];
    expect(teacherSpy).toHaveBeenCalledWith(firstSession.teacher_id.toString());
  });

  it('should update component properties after fetching session', () => {
    const targetSession: Session = sessionsMock[0];
    const targetTeacher: Teacher = getAllTeachersResponseMock[targetSession.teacher_id - 1];
    const isParticipating = targetSession.users.includes(sessionService.sessionInformation!.id);

    component.ngOnInit();

    expect(component.session).toEqual(targetSession);
    expect(component.teacher).toEqual(targetTeacher);
    expect(component.isParticipate).toEqual(isParticipating);
  });

  // ----------------------------------------
  // back()
  // ----------------------------------------
  it('should navigate back when calling back()', () => {
    const historySpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historySpy).toHaveBeenCalled();
  });

  // ----------------------------------------
  // delete()
  // ----------------------------------------
  it('should delete session and show snackbar', () => {
    const routerSpy = jest.spyOn(router, 'navigate');
    const snackSpy = jest.spyOn(matSnackBar, 'open');

    ngZone.run(() => component.delete());

    expect(sessionApiService.delete).toHaveBeenCalledWith(sessionId);
    expect(snackSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });

  // ----------------------------------------
  // participate()
  // ----------------------------------------
  it('should call participate API and refresh session', () => {
    const sessionSpy = jest.spyOn(sessionApiService, 'participate');
    const refreshSpy = jest.spyOn(sessionApiService, 'detail');

    component.participate();

    expect(sessionSpy).toHaveBeenCalledWith(sessionId, sessionService.sessionInformation!.id.toString());
    expect(refreshSpy).toHaveBeenCalledWith(sessionId);
  });

  // ----------------------------------------
  // unParticipate()
  // ----------------------------------------
  it('should call unParticipate API and refresh session', () => {
    const sessionSpy = jest.spyOn(sessionApiService, 'unParticipate');
    const refreshSpy = jest.spyOn(sessionApiService, 'detail');

    component.unParticipate();

    expect(sessionSpy).toHaveBeenCalledWith(sessionId, sessionService.sessionInformation!.id.toString());
    expect(refreshSpy).toHaveBeenCalledWith(sessionId);
  });
});
