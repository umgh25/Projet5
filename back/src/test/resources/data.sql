-- profs de test
INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Hélène', 'THIERCELIN');

-- utilisateur admin existant
INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Admin', 'Admin', true, 'yoga@studio.com',
        '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');

-- utilisateur non admin existant (attendu par les tests)
-- email: user@test.com / password: password
INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Test', 'User', false, 'user@test.com',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOe.VeF7qZ/3GQ8LNk31DJ7tY79DbDeXO');