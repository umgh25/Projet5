package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test pour la classe UserService
@SpringBootTest
public class UserServiceTest {

    private UserService userService;
    private UserMocks userMocks = new UserMocks();

    @MockBean
    private UserRepository userRepository;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    private User user;
    private final Long USER_ID = 1L;
    // Initialisation avant chaque test
    @BeforeEach
    void setUp() {
        user = userMocks.createUser(USER_ID, "john@mail.com", "Test", "John", "test123", false, false);
    }
    // Tests pour la méthode delete
    @Nested
    @DisplayName("Tests for delete method")
    class DeleteTests {
        @Test
        @DisplayName("Should delete a user successfully")
        void delete_ShouldCallRepositoryDeleteById() {
            userService.delete(USER_ID);
            verify(userRepository, times(1)).deleteById(USER_ID);
        }
        // Tests pour les cas d'erreur dans la méthode delete
        @Test
        @DisplayName("Should handle null ID")
        void delete_ShouldHandleNullId() {
            userService.delete(null);
            verify(userRepository, times(1)).deleteById(null);
        }
    }
    // Tests pour la méthode findById
    @Nested
    @DisplayName("Tests for findById method")
    class FindByIdTests {
        @Test
        @DisplayName("Should return user when it exists")
        void findById_ShouldReturnUser_WhenUserExists() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            User result = userService.findById(USER_ID);

            assertNotNull(result);
            assertEquals(USER_ID, result.getId());
            assertEquals("john@mail.com", result.getEmail());
            assertEquals("John", result.getFirstName());
            assertEquals("Test", result.getLastName());
            verify(userRepository, times(1)).findById(USER_ID);
        }
        // Tests pour les cas d'erreur dans la méthode findById
        @Test
        @DisplayName("Should return null when user doesn't exist")
        void findById_ShouldReturnNull_WhenUserDoesNotExist() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
            User result = userService.findById(USER_ID);
            assertNull(result);
            verify(userRepository, times(1)).findById(USER_ID);
        }
        // Tests pour les cas d'erreur dans la méthode findById
        @Test
        @DisplayName("Should handle null ID")
        void findById_ShouldHandleNullId() {
            when(userRepository.findById(null)).thenReturn(Optional.empty());
            User result = userService.findById(null);
            assertNull(result);
            verify(userRepository, times(1)).findById(null);
        }
    }
}