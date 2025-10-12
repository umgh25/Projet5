package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

// Test unitaire pour UserDetailsServiceImpl
@SpringBootTest
public class UserDetailsServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    // Test pour vérifier que loadUserByUsername() retourne un UserDetailsImpl correct quand l'utilisateur existe
    @Test
    @DisplayName("loadUserByUsername devrait retourner un UserDetailsImpl quand l'utilisateur existe")
    public void loadUserByUsername_WithExistingUser_ShouldReturnUserDetails() {
        String email = "test@example.com";
        User mockUser = User.builder()
                .id(1L)
                .email(email)
                .firstName("Test")
                .lastName("User")
                .password("encoded_password")
                .admin(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserDetailsImpl);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertEquals(1L, userDetailsImpl.getId());
        assertEquals(email, userDetailsImpl.getUsername());
        assertEquals("Test", userDetailsImpl.getFirstName());
        assertEquals("User", userDetailsImpl.getLastName());
        assertEquals("encoded_password", userDetailsImpl.getPassword());
        verify(userRepository, times(1)).findByEmail(email);
    }
    // Test pour vérifier que loadUserByUsername() lance une UsernameNotFoundException quand l'utilisateur n'existe pas
    @Test
    @DisplayName("loadUserByUsername devrait lancer UsernameNotFoundException quand l'utilisateur n'existe pas")
    public void loadUserByUsername_WithNonExistingUser_ShouldThrowException() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(email)
        );
        
        assertEquals("User Not Found with email: " + email, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}