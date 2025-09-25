import { of } from "rxjs";
import { Teacher } from "src/app/interfaces/teacher.interface";
import { TeacherService } from "src/app/services/teacher.service";

export const teacherPath = 'api/teacher';

export const getAllTeachersResponseMock: Teacher[] = [
  {
    id: 1,
    lastName: 'Dupont',
    firstName: 'Jeanne',
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    id: 2,
    lastName: 'Martin',
    firstName: 'Kylian',
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    id: 3,
    lastName: 'Durand',
    firstName: 'Amélie',
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];

export const createTeacherServiceMock = (): Partial<jest.Mocked<TeacherService>> => ({
  all: jest.fn().mockReturnValue(of(getAllTeachersResponseMock)),
  detail: jest.fn().mockImplementation((id: string) =>
    of(getAllTeachersResponseMock.find((teacher) => teacher.id === +id))
  ),
});
