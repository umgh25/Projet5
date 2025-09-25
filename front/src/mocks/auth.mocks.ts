import { Router } from '@angular/router';
import { LoginRequest } from 'src/app/features/auth/interfaces/loginRequest.interface';
import { RegisterRequest } from 'src/app/features/auth/interfaces/registerRequest.interface';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';




export const authPath = 'api/auth';

export const sessionInformation: SessionInformation = {
  token:
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b2dhQHN0dWRpby5jb20iLCJpYXQiOjE3MzkyNjk0MDAsImV4cCI6MTczOTM1NTgwMH0.QBnFSkldurGOIjhHX-NYym9UXHCngbYp6ZdM_SsYnHxpGcUbQLsrGnunVrM6eLbtx2icTVtDK36Zj0yw0bdpfQ',
  type: 'Bearer',
  id: 3,
  username: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'Admin',
  admin: true,
};

export const loginRequestMock: LoginRequest = {
  email: 'yoga@studio.com',
  password: 'test!1234',
};

export const invalidLoginRequestMock: LoginRequest = {
  email: 'invalid-email',
  password: '123',
};

export const registerRequestMock: RegisterRequest = {
  email: 'test_user@mail.com',
  firstName: 'Test',
  lastName: 'User',
  password: 'password123',
};

export const invalidRegisterRequestMock: RegisterRequest = {
  email: 'invalid-email',
  firstName: '',
  lastName: '',
  password: '123',
};

export const createRouterMock = (): Partial<jest.Mocked<Router>> => ({
  navigate: jest.fn(),
});

export const createAuthServiceMock = (): Partial<jest.Mocked<AuthService>> => ({
  register: jest.fn(),
  login: jest.fn()
})
