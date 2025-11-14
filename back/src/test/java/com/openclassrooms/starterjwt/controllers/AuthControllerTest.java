package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    // Injection de MockMvc et ObjectMapper via le constructeur
    @Autowired
    public AuthControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    // On "mocke" les dépendances pour isoler le contrôleur
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Authentication authentication;
    private UserDetailsImpl userDetails;
    private User user;

    @BeforeEach
    public void setup() {
        // Création d’un utilisateur standard pour les tests
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .build();

        // Création de l’objet UserDetailsImpl associé
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .build();

        // Simulation de l’authentification renvoyant le UserDetails
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("Login successful - Should return JWT token and user details")
    public void testLoginSuccess() throws Exception {
        // Préparation de la requête de connexion
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Définition du comportement des mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Exécution de la requête POST et vérification du résultat
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

    @Test
    @DisplayName("Login with admin role - Should return admin flag as true")
    public void testLoginAsAdmin() throws Exception {
        // Requête pour un utilisateur administrateur
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("adminPass");

        // Création d’un utilisateur admin
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedAdminPassword")
                .admin(true)
                .build();

        // Détails de l’utilisateur admin
        UserDetailsImpl adminDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedAdminPassword")
                .admin(true)
                .build();

        // Simulation d’authentification pour l’admin
        Authentication adminAuth = mock(Authentication.class);
        when(adminAuth.getPrincipal()).thenReturn(adminDetails);

        // Comportement des mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(adminAuth);
        when(jwtUtils.generateJwtToken(adminAuth)).thenReturn("admin-jwt-token");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Vérification de la réponse avec admin = true
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-jwt-token"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @DisplayName("Login validation error - Should return 400 Bad Request")
    public void testLoginValidationError() throws Exception {
        // Requête vide (champs manquants)
        LoginRequest loginRequest = new LoginRequest();

        // Vérification d’une erreur de validation
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register successful - Should return success message")
    public void testRegisterSuccess() throws Exception {
        // Données valides pour l’inscription
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");

        // Simulation d’un email non existant et encodage du mot de passe
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Vérifie que l’inscription retourne un message de succès
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Register with existing email - Should return error message")
    public void testRegisterWithExistingEmail() throws Exception {
        // Données d’un utilisateur déjà existant
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");
        signupRequest.setFirstName("Existing");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");

        // Simulation d’un email déjà utilisé
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Vérifie qu’un message d’erreur est retourné
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    @DisplayName("Register validation error - Should return 400 Bad Request")
    public void testRegisterValidationError() throws Exception {
        // Requête invalide (email incorrect)
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid@");

        // Vérifie que la validation échoue
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register should encode password")
    public void testRegisterPasswordEncoding() throws Exception {
        // Données d’un nouvel utilisateur
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("secure@example.com");
        signupRequest.setFirstName("Secure");
        signupRequest.setLastName("User");
        signupRequest.setPassword("rawPassword");

        // Simulation d’un encodage correct
        when(userRepository.existsByEmail("secure@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedSecurePassword");

        // Exécution et vérification de l’encodage
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Vérifie que la méthode encode a bien été appelée
        verify(passwordEncoder).encode("rawPassword");
    }
}
