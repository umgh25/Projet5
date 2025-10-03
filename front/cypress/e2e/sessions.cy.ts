/// <reference types="cypress" />

describe('Sessions', () => {
  describe('Load Sessions', () => {
    beforeEach(() => {
      cy.interceptSessions({});
    });

    it('Display sessions', () => {
      cy.login({ type: 'user' });
      cy.wait('@getSessionsRequest').then((interception) => {
        const response = interception.response!.body;
        cy.get('.item').should('have.length', response.length).and('be.visible');
          response.forEach((session, index) => {
            if (index > 3) return;
            cy.checkSessionCard(index, session);
          });
        });
    });

    it('Display Create and Detail buttons if user is admin', () => {
      cy.login({ type: 'admin' });
      cy.wait('@getSessionsRequest');
      cy.checkSessionsButtonsDisplay(true);
    });

    it('Display only Detail button if user is not admin', () => {
      cy.login({ type: 'user' });
      cy.wait('@getSessionsRequest');
      cy.checkSessionsButtonsDisplay(false);
    });
  })

  describe('Session Details', () => {
    const index = 1;

    beforeEach(() => {
      cy.interceptSessions({})
    })

    it('Display session for an admin', () => {
      cy.login({ type: 'admin' });

      cy.wait('@getSessionsRequest').then((interception) => {
        const sessionId = interception.response!.body[index].id;
        const teacherId = interception.response!.body[index].teacher_id;

        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.interceptTeacher(teacherId);

        cy.get('.item').eq(index).find('button').contains('Detail').click();
        cy.url().should('include', `/sessions/detail/${sessionId}`);

        cy.wait('@getSessionDetailRequest');
        cy.wait('@getTeacherRequest');

        cy.checkDetailSessionButtonsDisplay(true, false);
      });
    });

    it('Display session detail for a user who will participate in the session', () => {
      cy.login({ type: 'user' });

      cy.wait('@getSessionsRequest').then((interception) => {
        const sessionId = interception.response!.body[index].id;
        const teacherId = interception.response!.body[index].teacher_id;
        const sessionName = interception.response!.body[index].name;

        cy.interceptSessionDetail({ sessionId, participate: true });
        cy.interceptTeacher(teacherId);

        cy.get('.item').eq(index).find('button').contains('Detail').click();
        cy.url().should('include', `/sessions/detail/${sessionId}`);

        cy.wait('@getSessionDetailRequest');
        cy.wait('@getTeacherRequest');

        cy.get('mat-card-title h1').should('contain', sessionName);
        cy.checkDetailSessionButtonsDisplay(false, true);
      });
    });

    it('Display session detail for a user who will not participate in the session', () => {

      cy.interceptSessions({})
      cy.login({ type: 'user' });

      cy.wait('@getSessionsRequest').then((interception) => {
        const index = 1;
        const sessionId = interception.response!.body[index].id;
        const teacherId = interception.response!.body[index].teacher_id;
        const sessionName = interception.response!.body[index].name;

        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.interceptTeacher(teacherId);

        cy.get('.item').eq(index).find('button').contains('Detail').click();
        cy.url().should('include', `/sessions/detail/${sessionId}`);

        cy.wait('@getSessionDetailRequest');
        cy.wait('@getTeacherRequest');

        cy.get('mat-card-title h1').should('contain', sessionName);

        cy.checkDetailSessionButtonsDisplay(false, false);
      });
    });
  })

  describe('Participate or not to a session', () => {
    const index = 1;

    beforeEach(() => {
      cy.interceptSessions({})
      cy.login({ type: 'user' });
    });

    it('Participate in a session', () => {
      cy.wait('@getSessionsRequest').then((interception) => {
        const sessionId = interception.response!.body[index].id;
        const teacherId = interception.response!.body[index].teacher_id;

        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.interceptTeacher(teacherId);

        cy.get('.item').eq(index).find('button').contains('Detail').click();
        cy.url().should('include', `/sessions/detail/${sessionId}`);

        cy.wait('@getSessionDetailRequest');
        cy.wait('@getTeacherRequest');

        cy.interceptParticipateToSession({ sessionId });
        cy.interceptSessionDetail({ sessionId, participate: true });
        cy.get('button').contains('Participate').click();

        cy.wait('@postParticipateRequest');
        cy.wait('@getSessionDetailRequest');

        cy.get('button').contains('Do not participate').should('be.visible');
      });
    });

    it('Do not participate in a session', () => {
      cy.wait('@getSessionsRequest').then((interception) => {
        const sessionId = interception.response!.body[index].id;
        const teacherId = interception.response!.body[index].teacher_id;

        cy.interceptSessionDetail({ sessionId, participate: true });
        cy.interceptTeacher(teacherId);

        cy.get('.item').eq(index).find('button').contains('Detail').click();
        cy.url().should('include', `/sessions/detail/${sessionId}`);

        cy.wait('@getSessionDetailRequest');
        cy.wait('@getTeacherRequest');

        cy.interceptUnparticipateToSession({ sessionId });
        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.get('button').contains('Do not participate').click();

        cy.wait('@deleteParticipateRequest');
        cy.wait('@getSessionDetailRequest');

        cy.get('button').contains('Participate').should('be.visible');
      });
    });
  })

  describe('Session actions', () => {
    beforeEach(() => {
      cy.interceptSessions({});
      cy.login({ type: 'admin' });
    })

    it('Create a session', () => {
      cy.wait('@getSessionsRequest').then((interception) => {
        const response = interception.response!.body;
        const newSession: Cypress.YogaSession = {
          id: 999,
          name: 'Session open to all',
          description: 'Everyone is welcome to join this session. No experience required. Just bring your mat and a smile!',
          date: new Date('2025-02-28'),
          teacher_id: 1,
          users: [],
          createdAt: new Date('2025-02-20'),
          updatedAt: new Date('2025-02-20'),
        }
        const formattedDate = new Date(newSession.date).toISOString().split('T')[0];

        cy.interceptCreateSession(newSession);
        cy.interceptTeachers();

        cy.get('button').contains('Create').click();
        cy.wait('@getTeachersRequest');

        cy.get('input[formControlName="name"]').type(newSession.name);
        cy.get('input[formControlName="date"]').type(formattedDate);
        cy.get('mat-select[formControlName="teacher_id"]').click();
        cy.get('mat-option').first().click();
        cy.get('textarea[formControlName="description"]').type(newSession.description);

        cy.interceptSessions({ action: 'POST', session: newSession });
        cy.get('button[type="submit"]').click();

        cy.wait('@getSessionsRequest').then((interception) => {
          const responseAfterCreation = interception.response!.body;
          cy.get('.item').should('have.length', responseAfterCreation.length).and('be.visible');
          expect(responseAfterCreation.length).to.eq(response.length + 1);
          cy.checkSessionCard(responseAfterCreation.length - 1, newSession);

          cy.wait('@createSessionRequest').its('request.body').should('deep.equal', {
            name: newSession.name,
            description: newSession.description,
            teacher_id: newSession.teacher_id,
            date: formattedDate,
          })

          cy.get('snack-bar-container').should('contain', 'Session created !');
          cy.url().should('include', '/sessions');
        });
      })

    })

    it('Edit a session', () => {
      const index = 0;

      cy.wait('@getSessionsRequest').then((interception) => {
        const sessionId = interception.response!.body[index].id;

        const updatedSession: Cypress.YogaSession = {
          id: sessionId,
          name: 'Updated session',
          description: 'This session has been updated. Please check the new description.',
          date: new Date('2025-03-01'),
          teacher_id: 1,
        }

        const formattedDate = new Date(updatedSession.date).toISOString().split('T')[0];

        cy.interceptUpdateSession({ sessionId: sessionId, session: updatedSession });
        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.interceptTeachers();

        // click on item of index
        cy.get('.item').eq(index).find('button').contains('Edit').click();
        cy.url().should('include', `/sessions/update/${updatedSession.id}`);

        cy.wait("@getSessionDetailRequest");

        cy.get('input[formControlName="name"]').clear().type(updatedSession.name);
        cy.get('input[formControlName="date"]').clear().type(formattedDate);
        cy.get('mat-select[formControlName="teacher_id"]').click();
        cy.wait('@getTeachersRequest');
        cy.get('mat-option').first().click();
        cy.get('textarea[formControlName="description"]').clear().type(updatedSession.description);

        cy.interceptSessions({ action: 'PUT', session: updatedSession });
        cy.get('button[type="submit"]').click();

        cy.wait('@getSessionsRequest').then((interception) => {
          const response = interception.response!.body;
          cy.get('.item').should('have.length', response.length).and('be.visible');
          cy.checkSessionCard(index, updatedSession);

          cy.wait('@updateSessionRequest').its('request.body').should('deep.equal', {
            name: updatedSession.name,
            description: updatedSession.description,
            teacher_id: updatedSession.teacher_id,
            date: formattedDate,
          })

          cy.get('snack-bar-container').should('contain', 'Session updated !');
          cy.url().should('include', '/sessions');
        });
      });
    })

    it('Delete a session', () => {
      const index = 0;

      cy.wait('@getSessionsRequest').then((interception) => {
        const response = interception.response!.body;
        const sessionId = response[index].id;
        const teacherId = response[index].teacher_id;

        cy.interceptDeleteSession({ sessionId });
        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.interceptTeacher(teacherId);


        cy.get('.item').eq(index).find('button').contains('Detail').click();

        cy.wait('@getSessionDetailRequest');
        cy.wait('@getTeacherRequest');

        cy.interceptSessions({ action: 'DELETE', session: interception.response!.body[index] });
        cy.get('button').contains('Delete').click();

        cy.wait('@getSessionsRequest').then((interception) => {
          const responseAfterDelete = interception.response!.body;
          expect(responseAfterDelete.length).to.eq(response.length - 1);
          expect(responseAfterDelete.map(session => session.id)).to.not.include(sessionId);


          cy.wait('@deleteSessionRequest').its('response.body').should('deep.equal', {
            success: true
          });
        });
      });
    });

    it('Should display error when creating session with empty required fields', () => {
      cy.wait('@getSessionsRequest');
      
      cy.interceptTeachers();
      cy.get('button').contains('Create').click();
      cy.wait('@getTeachersRequest');
      
      // Vérifier que le formulaire est invalide au départ
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Essayer de soumettre sans remplir les champs
      cy.get('input[formControlName="name"]').focus().blur();
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Remplir seulement le nom
      cy.get('input[formControlName="name"]').type('Test Session');
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Remplir la date
      cy.get('input[formControlName="date"]').type('2025-12-31');
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Sélectionner un professeur
      cy.get('mat-select[formControlName="teacher_id"]').click();
      cy.get('mat-option').first().click();
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Remplir la description - le bouton devrait être activé
      cy.get('textarea[formControlName="description"]').type('Description test');
      cy.get('button[type="submit"]').should('not.be.disabled');
    });

    it('Should display error when editing session with empty required fields', () => {
      const index = 0;

      cy.wait('@getSessionsRequest').then((interception) => {
        const sessionId = interception.response!.body[index].id;

        cy.interceptSessionDetail({ sessionId, participate: false });
        cy.interceptTeachers();

        // Cliquer sur Edit
        cy.get('.item').eq(index).find('button').contains('Edit').click();
        cy.url().should('include', `/sessions/update/${sessionId}`);

        cy.wait("@getSessionDetailRequest");
        cy.wait('@getTeachersRequest');

        // Vider le champ name
        cy.get('input[formControlName="name"]').clear();
        cy.get('button[type="submit"]').should('be.disabled');

        // Re-remplir le nom
        cy.get('input[formControlName="name"]').type('Updated name');
        cy.get('button[type="submit"]').should('not.be.disabled');

        // Vider la description
        cy.get('textarea[formControlName="description"]').clear();
        cy.get('button[type="submit"]').should('be.disabled');

        // Re-remplir la description
        cy.get('textarea[formControlName="description"]').type('Updated description');
        cy.get('button[type="submit"]').should('not.be.disabled');
      });
    });
  })
})