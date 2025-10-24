describe('Register', () => {
  it('Register successfull', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      body: {
        lastName: 'toto',
        firstName: 'toto',
        email: 'toto@toto.com',
        password: 'test!1234'
      }
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/login',
      }, []).as('login')

      cy.get('input[formControlName=firstName]').type('toto')
      cy.get('input[formControlName=lastName]').type('toto')
      cy.get('input[formControlName=email]').type('toto@toto.com')
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

    cy.get('input[formControlName=firstName]').type('toto')
    cy.get('input[formControlName=lastName]').type('toto')
    cy.get('input[formControlName=email]').type('toto@toto.com')
    cy.get('input[formControlName=password]').type(`${'test!1234'}{enter}{enter}`)
    cy.url().should('include', '/register')
    cy.get('.error').should('be.visible')
  });

  it('Register failed with empty fields', () => {
    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('toto')
    cy.get('input[formControlName=lastName]').type('toto')
    cy.get('input[formControlName=email]').type('toto@toto.com')
    cy.get('input[formControlName=password]').type('{enter}{enter}')
    cy.url().should('include', '/register')
    cy.get('button[type=submit]').should('be.disabled')
  });
});