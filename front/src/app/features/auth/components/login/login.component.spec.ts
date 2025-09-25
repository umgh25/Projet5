import { Router } from '@angular/router';
import { NgZone } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from '../../services/auth.service';
import { createAuthServiceMock, invalidLoginRequestMock, loginRequestMock, sessionInformation } from 'src/mocks/auth.mocks';


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let ngZone: NgZone;
  let router: Router;
  let sessionService: SessionService;
  let authService: jest.Mocked<AuthService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
      providers: [
        SessionService,
        { provide: AuthService, useValue: createAuthServiceMock() },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
    sessionService = TestBed.inject(SessionService);
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have methods', () => {
    expect(component.submit).toBeInstanceOf(Function);
  });

  it('form should be invalid when empty', () => {
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when email is empty', () => {
    component.form.controls.email.setValue('');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when email is invalid', () => {
    component.form.controls.email.setValue('invalid-email');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when password is empty', () => {
    component.form.controls.password.setValue('');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when password is too short', () => {
    component.form.controls.password.setValue('in');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when password is too long', () => {
    component.form.controls.password.setValue(
      'ThisIsAReallyLongPasswordThatShouldNotBeAllowed'
    );
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when filled with incorrect values', () => {
    component.form.setValue(invalidLoginRequestMock);
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be valid when filled with correct values', () => {
    component.form.setValue(loginRequestMock);
    expect(component.form.valid).toBeTruthy();
  });

  it('should call login method successfuly on submit', () => {
    const routerSpy = jest.spyOn(router, 'navigate');

    component.form.setValue(loginRequestMock);
    authService.login.mockReturnValueOnce(of(sessionInformation));
    const sessionServiceLoginSpy = jest.spyOn(sessionService, 'logIn');

    ngZone.run(() => {
      component.submit();
    });

    expect(authService.login).toHaveBeenCalledWith({
      email: component.form.controls.email.value,
      password: component.form.controls.password.value
    });

    expect(sessionServiceLoginSpy).toHaveBeenCalledWith(sessionInformation);
    expect(routerSpy).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBeFalsy();
  });


  it('should display error message on login failure', () => {
    const routerSpy = jest.spyOn(router, 'navigate');

    component.form.setValue(invalidLoginRequestMock);
    authService.login.mockReturnValueOnce(throwError(() => new Error('An error occurred')));
    const sessionServiceLoginSpy = jest.spyOn(sessionService, 'logIn');


    ngZone.run(() => {
      component.submit();
    });

    expect(authService.login).toHaveBeenCalledWith({
      email: component.form.controls.email.value,
      password: component.form.controls.password.value
    });

    expect(sessionServiceLoginSpy).not.toHaveBeenCalled();
    expect(routerSpy).not.toHaveBeenCalled();
    expect(component.onError).toBeTruthy();
  })
});