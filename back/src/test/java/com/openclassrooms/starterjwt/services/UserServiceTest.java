package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test pour la classe UserService
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final UserMocks userMocks = new UserMocks();
    private User user;

    private final Long USER_ID = 1L;

    // Initialisation avant chaque test
    @BeforeEach
    void setUp() {
        user = userMocks.createUser(USER_ID, "john@mail.com", "Test", "John", "test123", false, false);
    }

    // Tests pour la méthode delete
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

    // Tests pour la méthode findById
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

    @Test
    @DisplayName("Should handle null ID")
    void findById_ShouldHandleNullId() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        User result = userService.findById(null);

        assertNull(result);
        verify(userRepository, times(1)).findById(null);
    }
}
