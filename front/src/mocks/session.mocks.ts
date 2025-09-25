import { of } from "rxjs";
import { SessionInformation } from "src/app/interfaces/sessionInformation.interface";
import { SessionService } from "src/app/services/session.service";

export const adminRequestMock: SessionInformation = {
  token:
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5b2dhQHN0dWRpby5jb20iLCJpYXQiOjE3MzkyNjk0MDAsImV4cCI6MTczOTM1NTgwMH0.QBnFSkldurGOIjhHX-NYym9UXHCngbYp6ZdM_SsYnHxpGcUbQLsrGnunVrM6eLbtx2icTVtDK36Zj0yw0bdpfQ',
  type: 'Bearer',
  id: 3,
  username: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'Admin',
  admin: true,
};

export const createSessionServiceMock = (): Partial<jest.Mocked<SessionService>> => {
  let _isLogged = true;
  return {
    isLogged: _isLogged,
    sessionInformation: adminRequestMock,
    $isLogged: jest.fn().mockReturnValue(of(_isLogged)),
    logIn: jest.fn(),
    logOut: jest.fn()
  };
};
