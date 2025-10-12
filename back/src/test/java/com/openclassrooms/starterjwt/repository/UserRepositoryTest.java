package com.openclassrooms.starterjwt.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.User;

// Intégration test pour la couche repository de User
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryTest {

    private final UserRepository userRepository;
    private final UserMocks userMocks = new UserMocks();
    private User user;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Initialisation avant chaque test
    @BeforeEach
    public void setup() {
        user = userMocks.createUser(1L, "andre_test@mail.com", "Margot", "password!123", "private!123", false, false );
    }
    // Nettoyage après chaque test
    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }
    // Tests
    @Test
    public void shouldSaveUser() {
        User savedUserInDB = userRepository.save(user);
        assertNotNull(savedUserInDB.getId());
        assertEquals(user.getEmail(), savedUserInDB.getEmail());
        assertEquals(user.getLastName(), savedUserInDB.getLastName());
        assertEquals(user.getFirstName(), savedUserInDB.getFirstName());
        assertEquals(user.getPassword(), savedUserInDB.getPassword());
        assertEquals(user.isAdmin(), savedUserInDB.isAdmin());
    }
    // Retrouve tous les utilisateurs
    @Test
    public void shouldFindAllUsers() {
        assertNotNull(userRepository.findAll());
    }
    // Retrouve un utilisateur par son id
    @Test
    public void shouldFindUserById() {
        User savedUserInDB = userRepository.save(user);
        User userInDB = userRepository.findById(savedUserInDB.getId()).orElse(null);
        assertNotNull(userInDB);
        assertEquals(user.getEmail(), userInDB.getEmail());
        assertEquals(user.getLastName(), userInDB.getLastName());
        assertEquals(user.getFirstName(), userInDB.getFirstName());
        assertEquals(user.getPassword(), userInDB.getPassword());
        assertEquals(user.isAdmin(), userInDB.isAdmin());
    }
    // Met à jour un utilisateur
    @Test
    public void shouldDeleteUser() {
        User savedUserInDB = userRepository.save(user);
        userRepository.deleteById(savedUserInDB.getId());
        User userInDB = userRepository.findById(savedUserInDB.getId()).orElse(null);
        assertEquals(null, userInDB);
    }

}