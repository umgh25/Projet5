import { Session } from '../features/sessions/interfaces/session.interface';

export const sessionApiPath = 'api/session';

export const sessionsMock: Session[] = [
  {
    id: 1,
    name: 'Session 1',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor.',
    date: new Date('2025-03-01T00:00:00Z'),
    teacher_id: 1,
    users: [],
  },
  {
    id: 2,
    name: 'Session 2',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor.',
    date: new Date('2024-12-29T00:00:00Z'),
    teacher_id: 2,
    users: [],
  },
];