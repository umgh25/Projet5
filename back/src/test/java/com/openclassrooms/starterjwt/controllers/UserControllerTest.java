package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

import java.time.LocalDateTime;
import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    // MockMvc pour simuler les requêtes HTTP vers le contrôleur
    private MockMvc mockMvc;

    // Injection de MockMvc via le constructeur
    @Autowired
    public UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    // Services et mappers simulés avec @MockBean
    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    // Données de test
    private User testUser;
    private UserDto testUserDto;

    // Préparation des données avant chaque test
    @BeforeEach
    public void setup() {
        // Création d’un utilisateur de test
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Création du DTO correspondant à l'utilisateur
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail("test@example.com");
        testUserDto.setFirstName("Test");
        testUserDto.setLastName("User");
        testUserDto.setAdmin(false);
        testUserDto.setCreatedAt(testUser.getCreatedAt());
        testUserDto.setUpdatedAt(testUser.getUpdatedAt());

        // Mock du mapper pour convertir User → UserDto
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
    }

    // Test GET /api/user/{id} - succès
    @Test
    @DisplayName("GET /api/user/{id} - Success")
    public void testGetUserById_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));

        verify(userService).findById(1L);
        verify(userMapper).toDto(testUser);
    }

    // Test GET /api/user/{id} - utilisateur non trouvé
    @Test
    @DisplayName("GET /api/user/{id} -> User Not Found")
    public void testGetUserById_NotFound() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/user/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isNotFound());

        verify(userService).findById(99L);
        verify(userMapper, never()).toDto(any(User.class));
    }

    // Test GET /api/user/{id} - ID invalide (non numérique)
    @Test
    @DisplayName("GET /api/user/{id} -> Invalid ID")
    public void testGetUserById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/user/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findById(any());
    }

    // Test DELETE /api/user/{id} - suppression réussie par l'utilisateur lui-même
    @Test
    @DisplayName("DELETE /api/user/{id} -> Success")
    public void testDeleteUser_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        // Configuration manuelle du contexte de sécurité avec un utilisateur authentifié
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        testUser.getEmail(), "password", Collections.emptyList());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser.getEmail())))
                .andExpect(status().isOk());

        verify(userService).findById(1L);
        verify(userService).delete(1L);
    }

    // Test DELETE /api/user/{id} - tentative de suppression d’un autre utilisateur
    @Test
    @DisplayName("DELETE /api/user/{id} -> Unauthorized")
    public void testDeleteUser_Unauthorized() throws Exception {
        User differentUser = User.builder()
                .id(2L)
                .email("other@example.com")
                .firstName("Other")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .build();

        when(userService.findById(2L)).thenReturn(differentUser);

        mockMvc.perform(delete("/api/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isUnauthorized());

        verify(userService).findById(2L);
        verify(userService, never()).delete(anyLong());
    }

    // Test DELETE /api/user/{id} - utilisateur inexistant
    @Test
    @DisplayName("DELETE /api/user/{id} -> User Not Found")
    public void testDeleteUser_NotFound() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/user/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isNotFound());

        verify(userService).findById(99L);
        verify(userService, never()).delete(anyLong());
    }

    // Test DELETE /api/user/{id} - ID invalide
    @Test
    @DisplayName("DELETE /api/user/{id} - Invalid ID")
    public void testDeleteUser_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/user/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findById(any());
        verify(userService, never()).delete(anyLong());
    }

    // Test DELETE /api/user/{id} - utilisateur non authentifié
    @Test
    @DisplayName("DELETE /api/user/{id} -> No Authentication")
    public void testDeleteUser_NoAuthentication() throws Exception {
        // Suppression du contexte de sécurité
        SecurityContextHolder.clearContext();
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(delete("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).delete(anyLong());
    }
}
