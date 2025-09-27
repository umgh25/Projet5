import { adminRequestMock } from '../../src/mocks/session.mocks';
import { sessionsMock } from '../../src/mocks/session-api.mocks';
import { getAllTeachersResponseMock } from '../../src/mocks/teacher.mocks';


Cypress.Commands.add('login', ({ email = 'yoga@studio.com', password = 'test!1234', type }) => {
  const selectors = {
    emailInput: 'input[formControlName=email]',
    passwordInput: 'input[formControlName=password]',
    errorMessage: '.error'
  };

  cy.visit('/login')
  cy.interceptLogin({ admin: type === 'admin' });

  if (!email || !password) {
    cy.get(selectors.emailInput).type('{enter}');
    cy.get(selectors.passwordInput).type('{enter}{enter}');
    cy.url().should('include', '/login');
    cy.get(selectors.errorMessage).should('be.visible').and('contain', 'An error occurred');
    return;
  }

  // Fill in login form
  cy.get(selectors.emailInput).type(email);
  cy.get(selectors.passwordInput).type(password + '{enter}{enter}');
  cy.wait('@loginRequest')

  // Check URL and session based on credentials
  if (email === 'yoga@studio.com' && password === 'test!1234') {
    cy.url().should('include', '/sessions');
  } else {
    cy.url().should('include', '/login');
    cy.get(selectors.errorMessage).should('be.visible').and('contain', 'An error occurred');
  }
})

Cypress.Commands.add('interceptLogin', ({ admin }) => {
  const loginResponse = {
    success: {
      statusCode: 200,
      body: {
        ...adminRequestMock,
        admin: admin,
      }
    },
    failure: {
      statusCode: 401,
      body: {
        message: 'Invalid credentials'
      }
    }
  };
  cy.intercept('POST', '/api/auth/login', req => {
    if (req.body.email === 'yoga@studio.com' && req.body.password === 'test!1234') {
      req.reply(loginResponse.success);
    } else {
      req.reply(loginResponse.failure);
    }
  }).as('loginRequest');
})

Cypress.Commands.add('interceptSessions', ({ action, session}) => {
  let sessions = sessionsMock;
  switch (action) {
    case 'POST':
      console.warn('POST');
      sessions = [...sessions, session];
      break;
    case 'PUT':
      console.warn('PUT');
      sessions = sessions.map(item =>
        item.id === session!.id ? { ...item, ...session } : item
      );
      break;
    case 'DELETE':
      console.warn('DELETE');
      sessions = sessions.filter(item => item.id !== session!.id);
      break;
    default:
      break;
  }

  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: sessions,
  }).as('getSessionsRequest');
});

Cypress.Commands.add('interceptSessionDetail', ({ sessionId, userId = 3, participate = false }) => {
  const index = sessionsMock.findIndex(session => session.id === sessionId);
  const sessionData = {
    ...sessionsMock[index],
    users: participate ? [...sessionsMock[index].users, userId] : sessionsMock[index].users.filter((user: number) => user !== userId)
  }

  console.log(sessionData);

  cy.intercept('GET', `/api/session/${sessionId}`, {
    statusCode: 200,
    body: sessionData,
  }).as('getSessionDetailRequest');
});

Cypress.Commands.add('interceptTeachers', () => {
  cy.intercept('GET', '/api/teacher', {
    statusCode: 200,
    body: getAllTeachersResponseMock,
  }).as('getTeachersRequest');
});

Cypress.Commands.add('interceptTeacher', (id) => {
  cy.intercept('GET', `/api/teacher/${id}`, {
    statusCode: 200,
    body: getAllTeachersResponseMock.find(teacher => teacher.id === id),
  }).as('getTeacherRequest');
});

Cypress.Commands.add('interceptParticipateToSession', ({ sessionId, userId = 3}) => {
  cy.intercept('POST', `/api/session/${sessionId}/participate/${userId}`, {
    statusCode: 200,
    body: { success: true }
  }).as('postParticipateRequest');
});

Cypress.Commands.add('interceptUnparticipateToSession', ({ sessionId, userId = 3 }) => {
  cy.intercept('DELETE', `/api/session/${sessionId}/participate/${userId}`, {
    statusCode: 200,
    body: { success: true }
  }).as('deleteParticipateRequest');
});

Cypress.Commands.add('interceptCreateSession', (session) => {

  cy.intercept('POST', '/api/session', {
    statusCode: 200,
    body: session
  }).as('createSessionRequest');
});

Cypress.Commands.add('interceptUpdateSession', ({ sessionId, session }) => {
  cy.intercept('PUT', `/api/session/${sessionId}`, {
    statusCode: 200,
    body: session
  }).as('updateSessionRequest');
});

Cypress.Commands.add('interceptDeleteSession', ({ sessionId }) => {
  cy.intercept('DELETE', `/api/session/${sessionId}`, {
    statusCode: 200,
    body: { success: true }
  }).as('deleteSessionRequest');
});

Cypress.Commands.add('interceptUser', ({ user }) => {
  const userId = user.id;
  console.warn(userId);
  cy.intercept('GET', `/api/user/${userId}`, {
    statusCode: 200,
    body: {
      id:  user.id,
      email: user.username,
      lastName: user.lastName,
      firstName: user.firstName,
      admin: user.admin,
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  }).as('getUserRequest');
});

Cypress.Commands.add('checkSessionCard', (index, session) => {
  cy.get('.item').eq(index).within(() => {
    cy.get('mat-card-title').should('contain', session.name);
    cy.get('mat-card-subtitle').should('contain', new Date(session.date).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }));
    cy.get('mat-card-content p').should('contain', session.description);
  });
});

Cypress.Commands.add('checkSessionsButtonsDisplay', (admin) => {
  cy.get('.item').first().find('button').contains('Detail').should('be.visible');
  cy.get('.item').eq(1).find('button').contains('Edit').should(admin ?'be.visible' : 'not.exist');
  cy.get('button').contains('Create').should(admin ? 'be.visible' : 'not.exist');
})

Cypress.Commands.add('checkDetailSessionButtonsDisplay', (admin, participate) => {
  if (admin) {
    cy.get('button').contains('Participate').should('not.exist');
    cy.get('button').contains('Do not participate').should('not.exist');
  } else {
    cy.get('button').contains('Participate').should(participate ? 'not.exist' : 'be.visible');
    cy.get('button').contains('Do not participate').should(participate ? 'be.visible' : 'not.exist');
  }
});