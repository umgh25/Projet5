import { of } from 'rxjs';
import { User } from '../interfaces/user.interface';
import { UserService } from '../services/user.service';

export const userPath = 'api/user';

export const userMock: User = {
  id: 1,
  email: 'test_user@mail.com',
  lastName: 'User',
  firstName: 'Test',
  admin: false,
  password: 'password123',
  createdAt: new Date(),
};

export const createUserServiceMock = (): Partial<jest.Mocked<UserService>> => ({
  getById: jest.fn().mockReturnValue(of(userMock)),
  delete: jest.fn().mockReturnValue(of({})),
});