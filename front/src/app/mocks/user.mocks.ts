import { User } from '../interfaces/user.interface';

export const userPath = 'api/user';

export const getUserResponseMock: User = {
  id: 1,
  email: 'test_user@mail.com',
  lastName: 'User',
  firstName: 'Test',
  admin: false,
  password: 'password123',
  createdAt: new Date(),
};