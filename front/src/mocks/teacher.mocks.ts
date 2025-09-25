import { of } from "rxjs";
import { Teacher } from "src/app/interfaces/teacher.interface";
import { TeacherService } from "src/app/services/teacher.service";



export const teacherPath = 'api/teacher';

export const getAllTeachersResponseMock: Teacher[] = [
  {
    id: 1,
    lastName: 'Teacher 1',
    firstName: 'Test 1',
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    id: 2,
    lastName: 'Teacher 2',
    firstName: 'Test 2',
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    id: 3,
    lastName: 'Teacher 3',
    firstName: 'Test 2',
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
