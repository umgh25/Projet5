describe('Register', () => {
  it('Register successfull', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      body: {
        lastName: 'Doe',
        firstName: 'John',
        email: 'john.doe@mail.com',
        password: 'Password!123'
      }
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/login',
      }, []
    ).as('login')

    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('john.doe@mail.com')
    cy.get('input[formControlName=password]').type(`${'Password!123'}{enter}{enter}`)

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

    cy.get('input[formControlName=firstName]').type('Jane')
    cy.get('input[formControlName=lastName]').type('Smith')
    cy.get('input[formControlName=email]').type('jane.smith@mail.com')
    cy.get('input[formControlName=password]').type(`${'WrongPass123!'}{enter}{enter}`)

    cy.url().should('include', '/register')
    cy.get('.error').should('be.visible')
  });

  it('Register failed with empty fields', () => {
    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('Alice')
    cy.get('input[formControlName=lastName]').type('Martin')
    cy.get('input[formControlName=email]').type('alice.martin@mail.com')
    cy.get('input[formControlName=password]').type('{enter}{enter}')

    cy.url().should('include', '/register')
    cy.get('button[type=submit]').should('be.disabled')
  });
});
