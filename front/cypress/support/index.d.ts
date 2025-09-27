declare namespace Cypress {
  interface Chainable<Subject = any> {
    login({ email, password, type }: LoginRequest ): void
    interceptLogin({ admin }: { admin: boolean }):void
    interceptSessions({ action, session }: SessionsRequest): void
    interceptSessionDetail({ sessionId, userId, participate }: SessionDetailRequest ): void
    interceptTeachers(): void
    interceptTeacher(id: number): void
    interceptParticipateToSession({ sessionId, userId }: participateRequest ): void
    interceptUnparticipateToSession({ sessionId, userId }: participateRequest ): void
    interceptCreateSession(session: YogaSession): void
    interceptUpdateSession({ sessionId, session }: { sessionId: number, session: YogaSession }): void
    interceptUser({ user }: { user: UserInformations }): void
    interceptDeleteSession({ sessionId }: { sessionId: number }): void
    checkSessionCard(index: number, session: YogaSession): void
    checkDetailSessionButtonsDisplay(admin: boolean, participate: boolean): void
    checkSessionsButtonsDisplay(admin: boolean): void
  }

  type LoginRequest = {
    email?: string;
    password?: string;
    type: 'admin' | 'user';
  }

  type SessionDetailRequest = {
    sessionId: number;
    userId?: number;
    participate: boolean;
  }

  type participateRequest = {
    sessionId: number;
    userId?: number;
  }

  type YogaSession = {
    id?: number;
    name: string;
    description: string;
    date: Date;
    teacher_id: number;
    users?: number[];
    createdAt?: Date;
    updatedAt?: Date;
  }

  type SessionsRequest = {
    action?: 'GET' | 'POST' | 'PUT' | 'DELETE';
    session?: YogaSession;
  }

  type UserInformations = {
    token: string;
    type: string;
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    admin: boolean;
  }
}