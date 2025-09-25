import { of } from 'rxjs';
import { Session } from 'src/app/features/sessions/interfaces/session.interface';
import { SessionApiService } from 'src/app/features/sessions/services/session-api.service';



export const sessionApiPath = 'api/session';

export const sessionsMock: Session[] = [
  {
    id: 1,
    name: 'Session 1',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor.',
    date: new Date('2025-03-01T00:00:00Z'),
    teacher_id: 1,
    users: [1, 2, 3],
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

export const createSessionApiServiceMock = (): Partial<jest.Mocked<SessionApiService>> => ({
  all: jest.fn().mockReturnValue(of(sessionsMock)),
  detail: jest.fn().mockImplementation((id: string) => of(sessionsMock.find((session) => session.id === +id))),
  delete: jest.fn().mockReturnValue(of(void 0)),
  create: jest.fn().mockImplementation((session: Session) => of({ ...session, id: 3 })),
  update: jest.fn().mockImplementation((id: string, session: Session) => of(session)),
  participate: jest.fn().mockReturnValue(of(void 0)),
  unParticipate: jest.fn().mockReturnValue(of(void 0)),
});
