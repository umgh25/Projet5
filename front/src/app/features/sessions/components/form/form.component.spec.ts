import { HttpClientModule } from '@angular/common/http';
import {
  ComponentFixture,
  TestBed
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';

import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { NgZone } from '@angular/core';

import { adminRequestMock, createSessionServiceMock } from 'src/mocks/session.mocks';
import { createSessionApiServiceMock, sessionsMock } from 'src/mocks/session-api.mocks';
import { createTeacherServiceMock } from 'src/mocks/teacher.mocks';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  let router: Router;
  let activatedRoute: ActivatedRoute;

  let sessionService: jest.Mocked<SessionService>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let teacherService: jest.Mocked<TeacherService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;
  let ngZone: NgZone;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: SessionService, useValue: createSessionServiceMock() },
        { provide: SessionApiService, useValue: createSessionApiServiceMock() },
        { provide: TeacherService, useValue: createTeacherServiceMock() },
        { provide: MatSnackBar, useValue: { open: jest.fn() } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: jest.fn() } } } },
      ],
      declarations: [FormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    teacherService = TestBed.inject(TeacherService) as jest.Mocked<TeacherService>;
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;
    activatedRoute = TestBed.inject(ActivatedRoute);

    ngZone = TestBed.inject(NgZone);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // --------------------------------------------
  // ngOnInit()
  // --------------------------------------------
  describe('ngOnInit', () => {
    it('should navigate to /sessions if user is not admin', () => {
      const routerSpy = jest.spyOn(router, 'navigate');

      sessionService.sessionInformation = {
        ...adminRequestMock,
        admin: false,
      };

      ngZone.run(() => component.ngOnInit());

      expect(routerSpy).toHaveBeenCalledWith(['/sessions']);
    });

    it('should initialize form for update', () => {
      jest.spyOn(activatedRoute.snapshot.paramMap, 'get').mockReturnValue('1');
      jest.spyOn(router, 'url', 'get').mockReturnValue(`/update/1`);

      component.ngOnInit();

      expect(component.onUpdate).toBe(true);
      expect(sessionApiService.detail).toHaveBeenCalledWith('1');

      expect(component.sessionForm?.value).toEqual({
        name: sessionsMock[0].name,
        date: new Date(sessionsMock[0].date).toISOString().split('T')[0],
        teacher_id: sessionsMock[0].teacher_id,
        description: sessionsMock[0].description,
      });
    });

    it('should initialize empty form when not editing', () => {
      jest.spyOn(activatedRoute.snapshot.paramMap, 'get').mockReturnValue(null);
      jest.spyOn(router, 'url', 'get').mockReturnValue('/');

      component.ngOnInit();

      expect(component.onUpdate).toBe(false);
      expect(component.sessionForm?.value).toEqual({
        name: '',
        date: '',
        teacher_id: '',
        description: '',
      });
    });
  });

  // --------------------------------------------
  // submit()
  // --------------------------------------------
  describe('submit', () => {
    it('should call create() and navigate with snackbar message (create mode)', () => {
      const snackSpy = jest.spyOn(matSnackBar, 'open');
      const routerSpy = jest.spyOn(router, 'navigate');

      component.onUpdate = false;
      component.sessionForm!.setValue({
        name: 'Test',
        date: '2025-02-11',
        teacher_id: 1,
        description: 'Test description',
      });

      ngZone.run(() => component.submit());

      expect(sessionApiService.create).toHaveBeenCalled();
      expect(snackSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(routerSpy).toHaveBeenCalledWith(['sessions']);
    });

    it('should call update() and navigate with snackbar message (update mode)', () => {
      const snackSpy = jest.spyOn(matSnackBar, 'open');
      const routerSpy = jest.spyOn(router, 'navigate');

      component.onUpdate = true;
      component.sessionForm!.setValue({
        name: 'Test',
        date: '2025-02-11',
        teacher_id: 1,
        description: 'Test description',
      });

      ngZone.run(() => component.submit());

      expect(sessionApiService.update).toHaveBeenCalled();
      expect(snackSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(routerSpy).toHaveBeenCalledWith(['sessions']);
    });
  });

  // --------------------------------------------
  // exitPage()
  // --------------------------------------------
  describe('exitPage', () => {
    it('should open snackbar and navigate to sessions', () => {
      const snackSpy = jest.spyOn(matSnackBar, 'open');
      const routerSpy = jest.spyOn(router, 'navigate');

      ngZone.run(() => component['exitPage']('Test message'));

      expect(snackSpy).toHaveBeenCalledWith('Test message', 'Close', { duration: 3000 });
      expect(routerSpy).toHaveBeenCalledWith(['sessions']);
    });
  });
});