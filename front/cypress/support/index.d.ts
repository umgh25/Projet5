/// <reference types="cypress" />

declare global {
  namespace Cypress {
    interface Chainable<Subject = any> {
      login(credentials: LoginRequest): Chainable<void>;
      interceptLogin(params: { admin: boolean }): Chainable<void>;
      interceptSessions(params: SessionsRequest): Chainable<void>;
      interceptSessionDetail(params: SessionDetailRequest): Chainable<void>;
      interceptTeachers(): Chainable<void>;
      interceptTeacher(id: number): Chainable<void>;
      interceptParticipateToSession(params: ParticipateRequest): Chainable<void>;
      interceptUnparticipateToSession(params: ParticipateRequest): Chainable<void>;
      interceptCreateSession(session: YogaSession): Chainable<void>;
      interceptUpdateSession(params: { sessionId: number; session: YogaSession }): Chainable<void>;
      interceptDeleteSession(params: { sessionId: number }): Chainable<void>;
      interceptUser(params: { user: UserInformations }): Chainable<void>;
      checkSessionCard(index: number, session: YogaSession): Chainable<void>;
      checkSessionsButtonsDisplay(admin: boolean): Chainable<void>;
      checkDetailSessionButtonsDisplay(admin: boolean, participate: boolean): Chainable<void>;
    }

    type LoginRequest = {
      email?: string;
      password?: string;
      type: 'admin' | 'user';
    };

    type SessionDetailRequest = {
      sessionId: number;
      userId?: number;
      participate: boolean;
    };

    type ParticipateRequest = {
      sessionId: number;
      userId?: number;
    };

    type YogaSession = {
      id?: number;
      name: string;
      description: string;
      date: Date;
      teacher_id: number;
      users?: number[];
      createdAt?: Date;
      updatedAt?: Date;
    };

    type SessionsRequest = {
      action?: 'GET' | 'POST' | 'PUT' | 'DELETE';
      session?: YogaSession;
    };

    type UserInformations = {
      token: string;
      type: string;
      id: number;
      username: string;
      firstName: string;
      lastName: string;
      admin: boolean;
    };
  }
}

// Important : cette ligne rend la déclaration globale effective
export {};
