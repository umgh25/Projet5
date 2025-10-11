package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

// Test d'intégration pour le AuthController
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    // Injection des dépendances nécessaires pour les tests
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    // Constructeur pour l'injection des dépendances
    @Autowired
    public AuthControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }
    // Mock des beans nécessaires pour les tests
    @MockBean
    private AuthenticationManager authenticationManager;
    // Mock du JwtUtils pour la génération de tokens JWT
    @MockBean
    private JwtUtils jwtUtils;
    // Mock du UserRepository pour les opérations sur les utilisateurs
    @MockBean
    private UserRepository userRepository;
    // Mock du PasswordEncoder pour l'encodage des mots de passe
    @MockBean
    private PasswordEncoder passwordEncoder;
    // Variables pour les tests
    private Authentication authentication;
    private UserDetailsImpl userDetails;
    private User user;
    // Configuration avant chaque test
    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .build();
        // Création d'un UserDetailsImpl pour simuler l'utilisateur authentifié
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .build();
        // Mock de l'Authentication pour simuler l'authentification réussie
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }
    // Test pour une connexion réussie
    @Test
    @DisplayName("Login successful - Should return JWT token and user details")
    public void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.admin").value(false));
    }
    // Test pour une connexion en tant qu'administrateur
    @Test
    @DisplayName("Login with admin role - Should return admin flag as true")
    public void testLoginAsAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("adminPass");
        
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedAdminPassword")
                .admin(true)
                .build();
        
        UserDetailsImpl adminDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedAdminPassword")
                .admin(true)
                .build();
        
        Authentication adminAuth = mock(Authentication.class);
        when(adminAuth.getPrincipal()).thenReturn(adminDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(adminAuth);
        when(jwtUtils.generateJwtToken(adminAuth)).thenReturn("admin-jwt-token");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-jwt-token"))
                .andExpect(jsonPath("$.admin").value(true));
    }
    // Test pour une erreur de validation lors de la connexion
    @Test
    @DisplayName("Login validation error - Should return 400 Bad Request")
    public void testLoginValidationError() throws Exception {
        LoginRequest loginRequest = new LoginRequest();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test pour une erreur d'authentification (mauvais mot de passe)
    @Test
    @DisplayName("Register successful - Should return success message")
    public void testRegisterSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");
        
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
    // Test pour une tentative d'inscription avec un email déjà existant
    @Test
    @DisplayName("Register with existing email - Should return error message")
    public void testRegisterWithExistingEmail() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");
        signupRequest.setFirstName("Existing");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }
    // Test pour une erreur de validation lors de l'inscription
    @Test
    @DisplayName("Register validation error - Should return 400 Bad Request")
    public void testRegisterValidationError() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid@");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test pour vérifier que le mot de passe est bien encodé lors de l'inscription
    @Test
    @DisplayName("Register should encode password")
    public void testRegisterPasswordEncoding() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("secure@example.com");
        signupRequest.setFirstName("Secure");
        signupRequest.setLastName("User");
        signupRequest.setPassword("rawPassword");

        when(userRepository.existsByEmail("secure@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedSecurePassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());
        verify(passwordEncoder).encode("rawPassword");
    }
}