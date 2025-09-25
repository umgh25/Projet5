import { NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { createAuthServiceMock, invalidRegisterRequestMock, registerRequestMock } from 'src/mocks/auth.mocks';



describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;
  let ngZone: NgZone;
  let authService: jest.Mocked<AuthService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: AuthService, useValue: createAuthServiceMock() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
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

  it('form should be invalid when first name is empty', () => {
    component.form.controls.firstName.setValue('');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when first name is too short', () => {
    component.form.controls.firstName.setValue('Te');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when first name is too long', () => {
    component.form.controls.firstName.setValue('ThisIsAReallyLongFirstName');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when last name is empty', () => {
    component.form.controls.lastName.setValue('');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when last name is too short', () => {
    component.form.controls.lastName.setValue('Us');
    expect(component.form.valid).toBeFalsy();
  });

  it('form should be invalid when last name is too long', () => {
    component.form.controls.lastName.setValue('ThisIsAReallyLongLastName');
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

  it('form should be valid when filled with correct values', () => {
    component.form.setValue(registerRequestMock);
    expect(component.form.valid).toBeTruthy();
  });

  it('should call register on submit and navigate to login on successful registration', () => {
    const routerSpy = jest.spyOn(router, 'navigate');

    component.form.setValue(registerRequestMock);
    authService.register.mockReturnValueOnce(of(void 0));

    ngZone.run(() => {
      component.submit();
    });

    expect(authService.register).toHaveBeenCalledWith(registerRequestMock);
    expect(routerSpy).toHaveBeenCalledWith(['/login']);
  });


  it('should display error message on registration failure', () => {
    const routerSpy = jest.spyOn(router, 'navigate');

    component.form.setValue(invalidRegisterRequestMock);
    authService.register.mockReturnValueOnce(throwError(() => new Error ('An error occurred')));

    component.submit();

    expect(authService.register).toHaveBeenCalledWith(invalidRegisterRequestMock);
    expect(routerSpy).not.toHaveBeenCalled();
    expect(component.onError).toBeTruthy();
  });
});