import { NgZone } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import {
  MatSnackBar,
  MatSnackBarModule,
} from '@angular/material/snack-bar';
import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { adminRequestMock, createSessionServiceMock } from 'src/mocks/session.mocks';
import { createSessionApiServiceMock, sessionsMock } from 'src/mocks/session-api.mocks';
import { createTeacherServiceMock, getAllTeachersResponseMock } from 'src/mocks/teacher.mocks';
import { Teacher } from 'src/app/interfaces/teacher.interface';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: Router;

  let sessionService: jest.Mocked<SessionService>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let teacherService: jest.Mocked<TeacherService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;
  let ngZone: NgZone;

  const sessionId: string = '1';

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
          useValue: {
            snapshot: { paramMap: { get: jest.fn().mockReturnValue(sessionId) } },
          }
        }
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);

    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    teacherService = TestBed.inject(TeacherService) as jest.Mocked<TeacherService>;
    sessionApiService = TestBed.inject(SessionApiService) as unknown as jest.Mocked<SessionApiService>;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shoudld have methods', () => {
    expect(component.ngOnInit).toBeDefined();
    expect(component.back).toBeInstanceOf(Function);
    expect(component.delete).toBeInstanceOf(Function);
    expect(component.participate).toBeInstanceOf(Function);
    expect(component.unParticipate).toBeInstanceOf(Function);
  });

  it('should initialize sessionId, userId and isAdmin on creation', () => {
    expect(component.sessionId).toBe(sessionId);
    expect(component.userId).toBe(adminRequestMock.id.toString());
    expect(component.isAdmin).toBe(adminRequestMock.admin);
  });

  it('should call fetchSession in ngOnInit', () => {
    const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
    component.ngOnInit();
    expect(fetchSessionSpy).toHaveBeenCalled();
  });

  it('should navigate back on calling back()', () => {
    const historySpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historySpy).toHaveBeenCalled();
  });

  it('should delete session and show snackbar on calling delete()', () => {
    ngZone = TestBed.inject(NgZone);

    const routerSpy = jest.spyOn(router, 'navigate');
    const matSnackBarSpy = jest.spyOn(matSnackBar, 'open');

    ngZone.run(() => {
      component.delete();
    });

    expect(sessionApiService.delete).toHaveBeenCalledWith(sessionId);
    expect(matSnackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should call participate and fetch session', () => {
    const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession')
    component.participate();
    expect(sessionApiService.participate).toHaveBeenCalledWith(sessionId, sessionService.sessionInformation!.id.toString());
    expect(fetchSessionSpy).toHaveBeenCalled();
  });

  it('should call unParticipate and fetch session', () => {
    const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession');
    component.unParticipate();
    expect(sessionApiService.unParticipate).toHaveBeenCalledWith(sessionId, sessionService.sessionInformation!.id.toString());
    expect(fetchSessionSpy).toHaveBeenCalled();
  });

  it('should fetch session and update properties', () => {
    const targetSession: Session = sessionsMock[parseInt(sessionId) - 1];
    const targetTeacher: Teacher = getAllTeachersResponseMock[targetSession.teacher_id - 1];
    const isParticipating: boolean = targetSession.users.some(u => u === sessionService.sessionInformation!.id);

    component['fetchSession']();
    expect(sessionApiService.detail).toHaveBeenCalledWith(sessionId);
    expect(teacherService.detail).toHaveBeenCalledWith(targetSession.teacher_id.toString());

    expect(component.session).toEqual(targetSession);
    expect(component.isParticipate).toEqual(isParticipating);
    expect(component.teacher).toEqual(targetTeacher);
  });
});