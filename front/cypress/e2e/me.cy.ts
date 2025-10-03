/// <reference types="cypress" />

describe('Account', () => {
  beforeEach(() => {
    cy.interceptSessions({});
  });

  it('Account page for admin', () => {
    cy.visit('/login');
    cy.interceptLogin({ admin: true });

    const selectors = {
      emailInput: 'input[formControlName=email]',
      passwordInput: 'input[formControlName=password]'
    };

    cy.get(selectors.emailInput).type('yoga@studio.com');
    cy.get(selectors.passwordInput).type('test!1234' + '{enter}{enter}');

    cy.wait('@loginRequest').then((interception) => {
      const user = interception.response!.body;

      cy.interceptUser({ user });
      cy.get('.link').contains('Account').should('be.visible');
      cy.get('.link').contains('Account').click();
      cy.url().should('include', '/me');

      cy.get('mat-card-content').should('contain', user.firstName);
      cy.get('mat-card-content').should('contain', user.lastName.toUpperCase());
      cy.get('mat-card-content').should('contain', user.username);
      cy.get('mat-card-content').should('contain', 'You are admin');
    });
  });

  it('Account page for user', () => {
    cy.visit('/login');
    cy.interceptLogin({ admin: false });

    const selectors = {
      emailInput: 'input[formControlName=email]',
      passwordInput: 'input[formControlName=password]',
      errorMessage: '.error',
    };

    cy.get(selectors.emailInput).type('yoga@studio.com');
    cy.get(selectors.passwordInput).type('test!1234' + '{enter}{enter}');

    cy.wait('@loginRequest').then((interception) => {
      const user = interception.response!.body;
      console.log(user);

      cy.interceptUser({ user });
      cy.get('.link').contains('Account').should('be.visible');
      cy.get('.link').contains('Account').click();
      cy.url().should('include', '/me');
    });
  });

  it('Logout', () => {
    cy.login({ type: 'user' });
    cy.get('.link').contains('Logout').should('be.visible');
    cy.get('.link').contains('Logout').click();
    cy.url().should('include', '/');
  });

  it("Delete account for users", () => {
    cy.visit("/login");
    cy.interceptLogin({ admin: false });

    const selectors = {
      emailInput: 'input[formControlName=email]',
      passwordInput: 'input[formControlName=password]',
      errorMessage: '.error',
    };

    cy.get(selectors.emailInput).type('yoga@studio.com');
    cy.get(selectors.passwordInput).type('test!1234' + '{enter}{enter}');

    cy.wait('@loginRequest').then((interception) => {
      const user = interception.response!.body;

      cy.interceptUser({ user });
      cy.get('.link').contains('Account').should('be.visible');
      cy.get('.link').contains('Account').click();
      cy.url().should('include', '/me');

      cy.intercept('DELETE', `/api/user/${user.id}`, {
        statusCode: 200,
        body: { success: true }
      }).as('deleteUserRequest');

      cy.contains('Delete my account:').should('be.visible');
      cy.get('button').contains('Detail').should('be.visible');
      cy.get('button').contains('Detail').click();

      cy.wait('@deleteUserRequest').then(() => {
        cy.contains('Your account has been deleted !').should('be.visible');
        cy.url().should('include', '/');
        cy.visit('/sessions');
        cy.url().should('include', '/login');
      });
    });
  });
});