/// <reference types="cypress" />

describe('Login spec', () => {
  it('Login successfull', () => {
    cy.login({ email: 'yoga@studio.com', password: 'test!1234', type: 'user' })
  })

  it('Login failed', () => {
    cy.login({ email: 'user@mail.com', password: 'password-1234', type: 'user' })
  })

  it('Login failed with empty fields', () => {
    cy.login({ email: '', password: '', type: 'user' })
  })
});