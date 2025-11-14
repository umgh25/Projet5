package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

// Test unitaire pour UserDetailsImpl
public class UserDetailsImplTest {

    @Test
    @DisplayName("Builder should create correct object")
    void builder_ShouldCreateCorrectObject() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(true)
                .build();

        assertEquals(1L, userDetails.getId());
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("Test", userDetails.getFirstName());
        assertEquals("User", userDetails.getLastName());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAdmin());
    }

    // Test pour vérifier que getAuthorities() retourne une collection vide
    @Test
    @DisplayName("getAuthorities should return an empty collection")
    void getAuthorities_ShouldReturnEmptyCollection() {
        UserDetailsImpl userDetails = createTestUserDetails();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    // Test pour vérifier que getPassword() retourne le mot de passe correct
    @Test
    @DisplayName("getPassword should return the correct password")
    void getPassword_ShouldReturnCorrectPassword() {
        UserDetailsImpl userDetails = createTestUserDetails();
        assertEquals("password", userDetails.getPassword());
    }

    // Test pour vérifier que isAccountNonExpired() retourne true
    @Test
    @DisplayName("isAccountNonExpired should return true")
    void isAccountNonExpired_ShouldReturnTrue() {
        UserDetailsImpl userDetails = createTestUserDetails();
        assertTrue(userDetails.isAccountNonExpired());
    }

    // Test pour vérifier que isAccountNonLocked() retourne true
    @Test
    @DisplayName("isAccountNonLocked should return true")
    void isAccountNonLocked_ShouldReturnTrue() {
        UserDetailsImpl userDetails = createTestUserDetails();
        assertTrue(userDetails.isAccountNonLocked());
    }

    // Test pour vérifier que isCredentialsNonExpired() retourne true et que le compte est actif
    @Test
    @DisplayName("isCredentialsNonExpired and isEnabled should return true")
    void isCredentialsNonExpired_ShouldReturnTrue() {
        UserDetailsImpl userDetails = createTestUserDetails();
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    // Test pour vérifier que equals() retourne true pour des IDs identiques
    @Test
    @DisplayName("equals should return true for objects with the same ID")
    void equals_WithSameId_ShouldReturnTrue() {
        UserDetailsImpl userDetails1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl userDetails2 = UserDetailsImpl.builder().id(1L).build();

        assertEquals(userDetails1, userDetails2);
    }

    // Test pour vérifier que equals() retourne false pour des IDs différents
    @Test
    @DisplayName("equals should return false for objects with different IDs")
    void equals_WithDifferentIds_ShouldReturnFalse() {
        UserDetailsImpl userDetails1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl userDetails2 = UserDetailsImpl.builder().id(2L).build();

        assertNotEquals(userDetails1, userDetails2);
    }

    // Test pour vérifier que equals() retourne false pour null ou un autre type
    @Test
    @DisplayName("equals should return false for null or different type")
    void equals_WithNullOrDifferentType_ShouldReturnFalse() {
        UserDetailsImpl userDetails = createTestUserDetails();

        assertNotEquals(userDetails, null);
        assertNotEquals(userDetails, new Object());
    }

    // Méthode utilitaire pour créer un UserDetailsImpl de test
    private UserDetailsImpl createTestUserDetails() {
        return UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();
    }
}
