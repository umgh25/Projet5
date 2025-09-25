import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from '../features/auth/interfaces/loginRequest.interface';
import { RegisterRequest } from '../features/auth/interfaces/registerRequest.interface';
import { Router } from '@angular/router';
import { SessionService } from '../services/session.service';

export const authPath = 'api/auth';

export const sessionInformation: SessionInformation = {
  token:
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2ODc4YzUwNGVlNzJlZmQ0NTNmMWNkOWQiLCJlbWFpbCI6InUubXVnaGFsQHR4LXN0dWRpby5jb20iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3NTc0MTEzMDUsImV4cCI6MTc1NzQzNjUwNX0.-LTk0sxSXr71xcc8mrpwPalWxGBPRyDCzTnAaTwiYfo',
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