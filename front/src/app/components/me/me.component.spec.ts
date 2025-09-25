import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { createRouterMock, sessionInformation } from 'src/mocks/auth.mocks';
import { createUserServiceMock, userMock } from 'src/mocks/user.mocks';



describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let sessionService: SessionService;
  let router: jest.Mocked<Router>;
  let userService: jest.Mocked<UserService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
       providers: [
        SessionService,
        { provide: Router, useValue: createRouterMock() },
        { provide: UserService, useValue: createUserServiceMock()},
        { provide: MatSnackBar, useValue: { open: jest.fn() } }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router) as jest.Mocked<Router>;
    sessionService = TestBed.inject(SessionService);
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;

    sessionService.logIn(sessionInformation);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have methods', () => {
    expect(component.ngOnInit).toBeDefined();
    expect(component.back).toBeInstanceOf(Function);
    expect(component.delete).toBeInstanceOf(Function);
  });

  it('should call userService.getById on ngOnInit', () => {
    const authenticatedUserId = sessionService.sessionInformation!.id.toString();
    component.ngOnInit();
    expect(userService.getById).toHaveBeenCalledWith(authenticatedUserId);
    expect(component.user).toEqual(userMock);
  });

  it('should call window.history.back() on back()', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(backSpy).toHaveBeenCalled();
  });


  it('should call userService.delete, then sessionService.logOut and navigate', () => {
    expect(sessionService.isLogged).toBe(true);

    const authenticatedUserId = sessionService.sessionInformation!.id.toString();
    const matSnackBarSpy = jest.spyOn(matSnackBar, 'open');
    const routerSpy = jest.spyOn(router, 'navigate');
    const logOutSpy = jest.spyOn(sessionService, 'logOut');

    component.delete();

    expect(userService.delete).toHaveBeenCalledWith(authenticatedUserId);
    expect(matSnackBarSpy).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(logOutSpy).toHaveBeenCalled();
    expect(sessionService.isLogged).toBe(false);
    expect(routerSpy).toHaveBeenCalledWith(['/']);
  });
});