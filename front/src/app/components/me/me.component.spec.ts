import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { Router } from '@angular/router';
import { createRouterMock, sessionInformation } from 'src/app/mocks/auth.mocks';
import { createSessionServiceMock } from 'src/app/mocks/session.mocks';
import { UserService } from 'src/app/services/user.service';
import { createUserServiceMock, userMock } from 'src/app/mocks/user.mocks';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let router: jest.Mocked<Router>;
  let sessionService: jest.Mocked<SessionService>;
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
        { provide: Router, useValue: createRouterMock() },
        { provide: SessionService, useValue: createSessionServiceMock() },
        { provide: UserService, useValue: createUserServiceMock()},
        { provide: MatSnackBar, useValue: { open: jest.fn() } }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router) as jest.Mocked<Router>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;


    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(typeof component.back).toBe('function');
    expect(typeof component.delete).toBe('function');
  });

  it('should call userService.getById on ngOnInit', () => {
    const connectedUser = userMock.id.toString();
    component.ngOnInit();
    userService.getById(connectedUser).subscribe((user) => {
      expect(user).toEqual(userMock);
      expect(user).toEqual(component.user);
    });
  });

  it('should call window.history.back() on back()', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(backSpy).toHaveBeenCalled();
  });

  it('should call userService.delete() on delete()', () => {
    sessionService.isLogged = true;
    const connectedUser = userMock.id.toString();
    const matSnackBarSpy = jest.spyOn(matSnackBar, 'open');
    const routerSpy = jest.spyOn(router, 'navigate');

    expect(sessionService.isLogged).toBe(true);

    component.delete();

    userService.delete(connectedUser).subscribe((_) => {
      expect(matSnackBarSpy).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
      expect(sessionService.logOut).toHaveBeenCalled();
      expect(sessionService.isLogged).toBe(false);
      expect(routerSpy).toHaveBeenCalledWith(['/']);
    });
  });


});