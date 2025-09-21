import { of } from 'rxjs';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { SessionService } from '../services/session.service';

export const userRequestMock: SessionInformation = {
  token:
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2ODc4YzUwNGVlNzJlZmQ0NTNmMWNkOWQiLCJlbWFpbCI6InUubXVnaGFsQHR4LXN0dWRpby5jb20iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3NTc0MTEzMDUsImV4cCI6MTc1NzQzNjUwNX0.-LTk0sxSXr71xcc8mrpwPalWxGBPRyDCzTnAaTwiYfo',
  type: 'Bearer',
  id: 3,
  username: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'Admin',
  admin: true,
};

export const createSessionServiceMock = (): Partial<jest.Mocked<SessionService>> => ({
  isLogged: false,
  sessionInformation: userRequestMock,
  $isLogged: jest.fn().mockReturnValue(of(false)),
  logIn: jest.fn(),
  logOut: jest.fn(),
});