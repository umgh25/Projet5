package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test d'intégration pour SessionController
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@WithMockUser
class SessionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Session testSession;
    private Teacher testTeacher;
    private Teacher testTeacher2;
    private Teacher testTeacher3;
    private User testUser;
    // Initialisation des données de test avant chaque test
    @BeforeEach
    void setUp() {
        // Mise en place de MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Nettoyage de la base de données
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        // Création d'enseignants de test
        testTeacher = Teacher.builder()
                .firstName("John")
                .lastName("YogaMaster")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testTeacher = teacherRepository.save(testTeacher);
        // Deuxième enseignant de test
        testTeacher2 = Teacher.builder()
                .firstName("Jane")
                .lastName("PilatesExpert")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testTeacher2 = teacherRepository.save(testTeacher2);
        // Troisième enseignant de test
        testTeacher3 = Teacher.builder()
                .firstName("Bob")
                .lastName("FitnessCoach")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testTeacher3 = teacherRepository.save(testTeacher3);

        // Création d'un utilisateur de test
        testUser = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password123")
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);

        // Création d'une session de test
        testSession = Session.builder()
                .name("Yoga Class")
                .description("Morning yoga session")
                .date(new Date())
                .teacher(testTeacher)
                .users(new ArrayList<>(Arrays.asList(testUser)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testSession = sessionRepository.save(testSession);
    }
    // Test d"intégration pour la recherche d'une session par ID
    @Test
    void findById_ShouldReturnSessionDto_WhenValidIdAndSessionExists() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/session/{id}", testSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testSession.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Yoga Class"))
                .andExpect(jsonPath("$.description").value("Morning yoga session"))
                .andExpect(jsonPath("$.teacher_id").value(testTeacher.getId().intValue()));
    }
    // Test d'intégration pour la recherche d'une session par ID inexistante
    @Test
    void findById_ShouldReturnNotFound_WhenValidIdButSessionDoesNotExist() throws Exception {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        mockMvc.perform(get("/api/session/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    // Test d'intégration pour la recherche d'une session avec un ID non numérique
    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/session/{id}", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

   // Test d'intégration pour la récupération de toutes les sessions
    @Test
    void findAll_ShouldReturnListOfSessions_WhenSessionsExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testSession.getId().intValue()))
                .andExpect(jsonPath("$[0].name").value("Yoga Class"))
                .andExpect(jsonPath("$[0].description").value("Morning yoga session"));
    }
    // Test d'intégration pour la récupération de toutes les sessions
    @Test
    void findAll_ShouldReturnEmptyList_WhenNoSessionsExist() throws Exception {
        // Arrange
        sessionRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Test d'intégration pour la création d'une session
    @Test
    void create_ShouldReturnSessionDto_WhenValidData() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New Session");
        sessionDto.setDescription("New session description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(testTeacher2.getId());

        // Act & Assert
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Session"))
                .andExpect(jsonPath("$.description").value("New session description"))
                .andExpect(jsonPath("$.teacher_id").exists());
    }
    // Test d'intégration pour la création d'une session avec un nom vide
    @Test
    void create_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("");
        sessionDto.setDescription("Description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(testTeacher2.getId());

        // Act & Assert
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }
    // Test d'intégration pour la création d'une session avec une date nulle
    @Test
    void create_ShouldReturnBadRequest_WhenDateIsNull() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Session");
        sessionDto.setDescription("Description");
        sessionDto.setDate(null);
        sessionDto.setTeacher_id(testTeacher2.getId());

        // Act & Assert
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }
    // Test d'intégration pour la création d'une session avec un enseignant nul
    @Test
    void create_ShouldReturnBadRequest_WhenTeacherIdIsNull() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Session");
        sessionDto.setDescription("Description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(null);

        // Act & Assert
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    // Test d'intégration pour la mise à jour d'une session avec des données valides
    @Test
    void update_ShouldReturnUpdatedSessionDto_WhenValidData() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Session");
        sessionDto.setDescription("Updated description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(testTeacher2.getId());

        // Act & Assert
        mockMvc.perform(put("/api/session/{id}", testSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }
    // Test d'intégration pour la mise à jour d'une session avec un ID non numérique
    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNotNumeric() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Session");
        sessionDto.setDescription("Updated description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(testTeacher2.getId());

        // Act & Assert
        mockMvc.perform(put("/api/session/{id}", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    // Test d'intégration pour la suppression d'une session existante
    @Test
    void delete_ShouldReturnOk_WhenSessionExists() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/session/{id}", testSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    // Test d'intégration pour la suppression d'une session inexistante
    @Test
    void delete_ShouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        mockMvc.perform(delete("/api/session/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    // Test d'intégration pour la suppression d'une session avec un ID non numérique
    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/session/{id}", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Test d'intégration pour participer à une session
    @Test
    void participate_ShouldReturnOk_WhenValidIds() throws Exception {
        // Arrange
        User newUser = User.builder()
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();
        newUser = userRepository.save(newUser);

        // Act & Assert
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", testSession.getId(), newUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    // Test d'intégration pour participer à une session avec un ID de session non numérique
    @Test
    void participate_ShouldReturnBadRequest_WhenSessionIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", "not-a-number", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    // Test d'intégration pour participer à une session avec un ID d'utilisateur non numérique
    @Test
    void participate_ShouldReturnBadRequest_WhenUserIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", testSession.getId(), "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Test d'intégration pour ne plus participer à une session
    @Test
    void noLongerParticipate_ShouldReturnOk_WhenValidIds() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", testSession.getId(), testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    // Test d'intégration pour ne plus participer à une session avec un ID de session non numérique
    @Test
    void noLongerParticipate_ShouldReturnBadRequest_WhenSessionIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", "not-a-number", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    // Test d'intégration pour ne plus participer à une session avec un ID d'utilisateur non numérique
    @Test
    void noLongerParticipate_ShouldReturnBadRequest_WhenUserIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", testSession.getId(), "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findById_ShouldNotAcceptPOST() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/session/{id}", testSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findAll_ShouldNotAcceptPOST_WithoutBody() throws Exception {
        // Act & Assert - This would actually work for create, but testing wrong usage
        mockMvc.perform(put("/api/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    // Tests pour les changements d'état de la base de données
    @Test
    void create_ShouldPersistSessionInDatabase() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Persisted Session");
        sessionDto.setDescription("This should be persisted");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(testTeacher2.getId());

        // Act
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk());

        // Assert
        long sessionCount = sessionRepository.count();
        assert sessionCount == 2; // Original + new one
    }
    // Test d'intégration pour la mise à jour d'une session dans la base de données
    @Test
    void delete_ShouldRemoveSessionFromDatabase() throws Exception {
        // Act
        mockMvc.perform(delete("/api/session/{id}", testSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        long sessionCount = sessionRepository.count();
        assert sessionCount == 0;
    }

    // Tests pour les JSON mal formés
    @Test
    void create_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed json"))
                .andExpect(status().isBadRequest());
    }
    // Test d'intégration pour la mise à jour d'une session avec un JSON mal formé
    @Test
    void update_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/session/{id}", testSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed json"))
                .andExpect(status().isBadRequest());
    }

    // Test d'intégration pour la création d'une session avec des caractères spéciaux dans le nom
    @Test
    void create_ShouldHandleSpecialCharactersInName() throws Exception {
        // Arrange
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga & Méditation");
        sessionDto.setDescription("Session with special chars: àáâãäå");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(testTeacher3.getId());

        // Act & Assert
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga & Méditation"))
                .andExpect(jsonPath("$.description").value("Session with special chars: àáâãäå"));
    }
}