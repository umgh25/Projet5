/// <reference types="cypress" />

describe('Register', () => {
  it('Register successfull', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      body: {
        lastName: 'John',
        firstName: 'Doe',
        email: 'John@Doe.com',
        password: 'test!1234'
      }
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/login',
      }, []).as('login')

      cy.get('input[formControlName=firstName]').type('John')
      cy.get('input[formControlName=lastName]').type('Doe')
      cy.get('input[formControlName=email]').type('John@Doe.com')
    cy.get('input[formControlName=password]').type(`${'test!1234'}{enter}{enter}`)
    cy.url().should('include', '/login')
  });

  it('Register failed', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 401,
      body: {
        message: 'Invalid credentials'
      },
    })

    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('John@Doe.com')
    cy.get('input[formControlName=password]').type(`${'test!1234'}{enter}{enter}`)
    cy.url().should('include', '/register')
    cy.get('.error').should('be.visible')
  });

  it('Register failed with empty fields', () => {
    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('John@Doe.com')
    cy.get('input[formControlName=password]').type('{enter}{enter}')
    cy.url().should('include', '/register')
    cy.get('button[type=submit]').should('be.disabled')
  });
});