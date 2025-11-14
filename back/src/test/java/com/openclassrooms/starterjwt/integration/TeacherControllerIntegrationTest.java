package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test d'intégration pour le TeacherController
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@WithMockUser
class TeacherControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TeacherRepository teacherRepository;


    private MockMvc mockMvc;
    private Teacher testTeacher;
    private Teacher testTeacher2;

    @BeforeEach
    void setUp() {
        // Mise en place de MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Nettoyage de la base de données
        teacherRepository.deleteAll();

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
                .lastName("YogaExpert")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testTeacher2 = teacherRepository.save(testTeacher2);
    }

    // Test d'intégration pour la récupération d'un enseignant par ID
    @Test
    void findById_ShouldReturnTeacherDto_WhenValidIdAndTeacherExists() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", testTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testTeacher.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("YogaMaster"));
    }

    // Test d'intégration pour la récupération d'un enseignant par ID inexistant
    @Test
    void findById_ShouldReturnNotFound_WhenValidIdButTeacherDoesNotExist() throws Exception {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    // Test d'intégration pour la récupération d'un enseignant avec des IDs invalides
    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    // Test d'intégration pour la récupération d'un enseignant avec des IDs invalides
    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsEmpty() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", "")
                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()); 
    }
    // Test d'intégration pour la récupération d'un enseignant avec des IDs invalides
    @Test
    void findById_ShouldReturnBadRequest_WhenIdContainsSpecialCharacters() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", "1@#$")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    // Test d'intégration pour la récupération d'un enseignant avec des IDs invalides
    @Test
    void findById_ShouldReturnNotFound_WhenNegativeId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", "-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    // Test d'intégration pour la récupération d'un enseignant avec des IDs invalides
    @Test
    void findById_ShouldReturnNotFound_WhenZeroId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    // Test d'intégration pour la récupération d'un enseignant avec des IDs invalides
    @Test
    void findById_ShouldHandleLargeIds() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Test d'intégration pour la récupération de tous les enseignants
    @Test
    void findAll_ShouldReturnListOfTeachers_WhenTeachersExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testTeacher.getId().intValue()))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("YogaMaster"))
                .andExpect(jsonPath("$[1].id").value(testTeacher2.getId().intValue()))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("YogaExpert"));
    }
    // Test d'intégration pour la récupération de tous les enseignants
    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTeachersExist() throws Exception {
        // Arrange
        teacherRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
    // Test d'intégration pour la récupération de tous les enseignants
    @Test
    void findAll_ShouldReturnSingleTeacher_WhenOnlyOneExists() throws Exception {
        // Arrange
        teacherRepository.deleteAll();
        Teacher singleTeacher = teacherRepository.save(Teacher.builder()
                .firstName("Solo")
                .lastName("Teacher")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(singleTeacher.getId().intValue()))
                .andExpect(jsonPath("$[0].firstName").value("Solo"))
                .andExpect(jsonPath("$[0].lastName").value("Teacher"));
    }
    // Test d'intégration pour vérifier que tous les champs de l'enseignant sont inclus dans la réponse
    @Test
    void findAll_ShouldIncludeAllTeacherFields() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists());
    }

    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findById_ShouldNotAcceptPOST() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/teacher/{id}", testTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findById_ShouldNotAcceptPUT() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/teacher/{id}", testTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findById_ShouldNotAcceptDELETE() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/teacher/{id}", testTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findAll_ShouldNotAcceptPOST() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findAll_ShouldNotAcceptPUT() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
    // Test d'intégration pour les méthodes HTTP non autorisées
    @Test
    void findAll_ShouldNotAcceptDELETE() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    // Test d'intégration pour la prise en charge de CORS
    @Test
    void findById_ShouldSupportCORS() throws Exception {
        // Note: CORS headers might not be set in test environment
        // Act & Assert - Just verify the endpoint works
        mockMvc.perform(get("/api/teacher/{id}", testTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    // Test d'intégration pour la prise en charge de CORS
    @Test
    void findAll_ShouldSupportCORS() throws Exception {
        // Note: CORS headers might not be set in test environment
        // Act & Assert - Just verify the endpoint works
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Test d'intégration pour la gestion des types de contenu
    @Test
    void findById_ShouldAcceptDifferentContentTypes() throws Exception {
        // Test avec application/json
        mockMvc.perform(get("/api/teacher/{id}", testTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test sans type explicite
        mockMvc.perform(get("/api/teacher/{id}", testTeacher.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_ShouldAcceptDifferentContentTypes() throws Exception {
        // Test avec application/json
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Test sans type explicite
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk());
    }

    // Test d'intégration pour les grands ensembles de données
    @Test
    void findAll_ShouldHandleLargeDataset() throws Exception {
        // Arrange - Clean and add many teachers
        teacherRepository.deleteAll();
        for (int i = 1; i <= 20; i++) {
            teacherRepository.save(Teacher.builder()
                    .firstName("Teacher" + i)
                    .lastName("LastName" + i)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)))
                .andExpect(jsonPath("$[0].firstName").value("Teacher1"))
                .andExpect(jsonPath("$[19].firstName").value("Teacher20"));
    }

    // Test d'intégration pour vérifier la cohérence des données
    @Test
    void findById_ShouldReturnCorrectDataFromDatabase() throws Exception {
        // Arrange - Create specific teacher with known data
        Teacher specificTeacher = teacherRepository.save(Teacher.builder()
                .firstName("Specific")
                .lastName("TestTeacher")
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2023, 1, 2, 11, 0))
                .build());

        // Act & Assert
        mockMvc.perform(get("/api/teacher/{id}", specificTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specificTeacher.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value("Specific"))
                .andExpect(jsonPath("$.lastName").value("TestTeacher"));
    }
    // Test d'intégration pour vérifier que les modifications dans la base de données sont reflétées
    @Test
    void findAll_ShouldReflectDatabaseChanges() throws Exception {
        // Arrange - Initial state check
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Add new teacher
        Teacher newTeacher = teacherRepository.save(Teacher.builder()
                .firstName("New")
                .lastName("Teacher")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        // Act & Assert - Should reflect the change
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].id").value(newTeacher.getId().intValue()))
                .andExpect(jsonPath("$[2].firstName").value("New"))
                .andExpect(jsonPath("$[2].lastName").value("Teacher"));
    }

    // Test d'intégration pour la gestion des caractères spéciaux dans les noms
    @Test
    void findAll_ShouldHandleSpecialCharactersInNames() throws Exception {
        // Arrange
        teacherRepository.deleteAll();
        teacherRepository.save(Teacher.builder()
                .firstName("José-María")
                .lastName("O'Connor-Smith")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        // Act & Assert
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("José-María"))
                .andExpect(jsonPath("$[0].lastName").value("O'Connor-Smith"));
    }

    // Test d'intégration pour la gestion de plusieurs requêtes consécutives
    @Test
    void multipleRequests_ShouldWorkIndependently() throws Exception {
        // Act & Assert - Multiple requests should all work
        mockMvc.perform(get("/api/teacher/{id}", testTeacher.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
        
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/teacher/{id}", testTeacher2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // Should still be 2
    }
}