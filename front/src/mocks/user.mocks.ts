import { of } from 'rxjs';
import { User } from 'src/app/interfaces/user.interface';
import { sessionInformation } from './auth.mocks';
import { UserService } from 'src/app/services/user.service';



export const userPath = 'api/user';

export const userMock: User = {
  id: sessionInformation.id,
  email: sessionInformation.username,
  firstName: sessionInformation.firstName,
  lastName: sessionInformation.lastName,
  admin: sessionInformation.admin,
  password: 'password123',
  createdAt: new Date(),
};

export const createUserServiceMock = (): Partial<jest.Mocked<UserService>> => ({
  getById: jest.fn().mockReturnValue(of(userMock)),
  delete: jest.fn().mockReturnValue(of(void 0)),
});
