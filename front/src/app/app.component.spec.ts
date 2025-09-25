import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { Router } from '@angular/router';
import { NgZone } from '@angular/core';
import { createSessionServiceMock } from 'src/mocks/session.mocks';


describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let router: Router;
  let ngZone: NgZone;
  let sessionService: jest.Mocked<SessionService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: SessionService, useValue: createSessionServiceMock() },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;

    fixture.detectChanges();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should call sessionService.$isLogged when $isLogged is called', () => {
    component.$isLogged();
    expect(sessionService.$isLogged).toHaveBeenCalled();
  });

  it('should call logOut and navigate when logout is called', () => {
    const routerSpy = jest.spyOn(router, 'navigate');
    ngZone.run(() => {
      component.logout();
    })
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['']);
  });
});