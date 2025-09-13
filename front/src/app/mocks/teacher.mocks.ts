import { Teacher } from '../interfaces/teacher.interface';

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