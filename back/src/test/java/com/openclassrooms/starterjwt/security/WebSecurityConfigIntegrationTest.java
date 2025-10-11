package com.openclassrooms.starterjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests d'intégration pour la configuration de sécurité Web
@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityConfigIntegrationTest {
    // Déclaration des dépendances nécessaires pour les tests
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    // Injection des dépendances nécessaires pour les tests
    @Autowired
    public WebSecurityConfigIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper; 
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Déclaration des objets de requête pour les tests
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private static final String TEST_EMAIL = "security.test@example.com";
    private static final String TEST_PASSWORD = "Test123!";
    // Initialisation des données de test avant chaque test
    @BeforeEach
    public void setup() {
        Optional<User> existingUser = userRepository.findByEmail(TEST_EMAIL);
        existingUser.ifPresent(user -> userRepository.delete(user));

        signupRequest = new SignupRequest();
        signupRequest.setEmail(TEST_EMAIL);
        signupRequest.setPassword(TEST_PASSWORD);
        signupRequest.setFirstName("Security");
        signupRequest.setLastName("Test");

        loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);
    }
    // Test pour vérifier que les endpoints d'authentification sont publics
    @Test
    @DisplayName("Auth endpoints are public")
    public void authEndpointsArePublic() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        assertTrue(userRepository.existsByEmail(TEST_EMAIL));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains(TEST_EMAIL) || 
                  responseContent.contains("Security") || 
                  responseContent.contains("Test"),
                  "La réponse devrait contenir des informations sur l'utilisateur");
    }
    // Test pour vérifier que les endpoints de l'API nécessitent une authentification
    @Test
    @DisplayName("Endpoints requiring authentication")
    public void apiEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());
    }
    // Test pour vérifier que les endpoints de l'API sont accessibles avec une authentification
    @Test
    @WithMockUser
    @DisplayName("API endpoints accessible with auth")
    public void apiEndpointsAccessibleWithAuth() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk());
    }
    // Test pour vérifier que les autres endpoints nécessitent une authentification
    @Test
    @DisplayName("Other endpoints require authentication")
    public void otherEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/random-endpoint"))
                .andExpect(status().isUnauthorized());
    }
    // Test pour vérifier que le filtre JWT fonctionne correctement
    @Test
    @DisplayName("JWT filter works")
    public void jwtFilterWorks() throws Exception {
        if (!userRepository.existsByEmail(TEST_EMAIL)) {
            User user = new User();
            user.setEmail(TEST_EMAIL);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            user.setFirstName("Security");
            user.setLastName("Test");
            user.setAdmin(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();
        String token = extractTokenFromLoginResponse(loginResult.getResponse().getContentAsString());
        mockMvc.perform(get("/api/session")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
    // Test pour vérifier que les tokens JWT invalides sont rejetés
    @Test
    @DisplayName("Invalid JWT token is rejected")
    public void invalidJwtTokenIsRejected() throws Exception {
        String invalidToken = "invalid.jwt.token";
        
        mockMvc.perform(get("/api/session")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    private String extractTokenFromLoginResponse(String loginResponse) throws Exception {
        return objectMapper.readTree(loginResponse).get("token").asText();
    }
}