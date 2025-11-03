package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Tests d'intégration pour AuthController
// Teste l'API REST complète avec le contexte Spring réel, la sécurité et la base de données H2
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    // Mise en place avant chaque test
    @BeforeEach
    void setUp() {
        // Initialisation de MockMvc avec le contexte Web
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Nettoyage de la base de données avant chaque test
        userRepository.deleteAll();
        // Création d'un utilisateur de test
        testUser = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);
    }

    // Test d'authentification réussie
    @Test
    void authenticateUser_ShouldReturnJwtResponse_WhenValidCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.id").value(testUser.getId().intValue()))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.admin").value(false));
    }
    // Test d'authentification avec email invalide
    @Test
    void authenticateUser_ShouldReturnBadRequest_WhenEmailEmpty() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test d'authentification avec mot de passe invalide
    @Test
    void authenticateUser_ShouldReturnBadRequest_WhenPasswordEmpty() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test d'authentification avec utilisateur admin
    @Test
    void authenticateUser_ShouldReturnJwtResponse_WhenUserIsAdmin() throws Exception {
        // Arrange
        User adminUser = User.builder()
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password(passwordEncoder.encode("adminpass"))
                .admin(true)
                .build();
        userRepository.save(adminUser);
        // Création d'une requête de connexion
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("adminpass");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true));
    }
    // Test d'enregistrement réussi
    @Test
    void registerUser_ShouldReturnSuccessMessage_WhenValidData() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
    // test d'enregistrement avec email déjà existant
    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }
    // Additional validation tests for registration
    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailInvalid() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalid-email");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test d'enregistrement avec mot de passe trop court
    @Test
    void registerUser_ShouldReturnBadRequest_WhenPasswordTooShort() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test d'enregistrement avec prénom trop court
    @Test
    void registerUser_ShouldReturnBadRequest_WhenFirstNameTooShort() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("A");
        signupRequest.setLastName("User");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test d'enregistrement avec nom de famille trop court
    @Test
    void registerUser_ShouldReturnBadRequest_WhenLastNameTooShort() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("A");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }
    // Test d'enregistrement avec email vide
    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailEmpty() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test des méthodes HTTP
    @Test
    void login_ShouldNotAcceptGET() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test des méthodes HTTP
    @Test
    void register_ShouldNotAcceptGET() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test des méthodes HTTP
    @Test
    void login_ShouldNotAcceptPUT() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test des méthodes HTTP
    @Test
    void register_ShouldNotAcceptPUT() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    // Test de la persistance des données
    @Test
    void registerUser_ShouldPersistUserInDatabase() throws Exception {
        // Arrange
        String newEmail = "persisted@example.com";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(newEmail);
        signupRequest.setFirstName("Persisted");
        signupRequest.setLastName("User");
        signupRequest.setPassword("persistedpass");

        // Act
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Assert
        User savedUser = userRepository.findByEmail(newEmail).orElse(null);
        assert savedUser != null;
        assert savedUser.getFirstName().equals("Persisted");
        assert savedUser.getLastName().equals("User");
        assert !savedUser.isAdmin();
    }
    // Test de la persistance des données avec mot de passe chiffré
    @Test
    void registerUser_ShouldEncryptPassword() throws Exception {
        // Arrange
        String newEmail = "encrypted@example.com";
        String plainPassword = "plainpassword";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(newEmail);
        signupRequest.setFirstName("Encrypted");
        signupRequest.setLastName("User");
        signupRequest.setPassword(plainPassword);

        // Act
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Assert
        User savedUser = userRepository.findByEmail(newEmail).orElse(null);
        assert savedUser != null;
        assert !savedUser.getPassword().equals(plainPassword);
        assert passwordEncoder.matches(plainPassword, savedUser.getPassword());
    }

    // Test des Content-Type
    @Test
    void login_ShouldReturnUnsupportedMediaType_WhenNoContentType() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }
    // Test des Content-Type
    @Test
    void register_ShouldReturnUnsupportedMediaType_WhenNoContentType() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("newpass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }
    // Test des Content-Type
    @Test
    void login_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed json"))
                .andExpect(status().isBadRequest());
    }
    // Test des Content-Type
    @Test
    void register_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed json"))
                .andExpect(status().isBadRequest());
    }
}