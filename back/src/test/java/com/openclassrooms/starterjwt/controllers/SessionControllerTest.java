package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mocks.SessionMocks;
import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {

    // MockMvc pour simuler les appels HTTP et ObjectMapper pour sérialiser/désérialiser les objets JSON
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // Mocks utilitaires pour créer des objets de test (Teacher, User, Session)
    private TeacherMocks teacherMocks = new TeacherMocks();
    private UserMocks userMocks = new UserMocks();
    private SessionMocks sessionMocks = new SessionMocks();

    // Injection de MockMvc et ObjectMapper via le constructeur
    @Autowired
    public SessionControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    // Dépendances du contrôleur simulées avec @MockBean
    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session testSession;
    private SessionDto testSessionDto;
    private List<Session> testSessions;
    private List<SessionDto> testSessionDtos;

    // Préparation des données avant chaque test
    @BeforeEach
    public void setup() {
        Teacher testTeacher = teacherMocks.createTeacher(1L, "Test", "Margot", false);
        User testUser = userMocks.createUser(1L, "andre_test@mail.com", "Test", "Teacher", "private!123", false, false);

        // Création d’une session et de son équivalent DTO
        testSession = sessionMocks.createSession(1L, testTeacher, Arrays.asList(testUser), false, false);
        testSessionDto = sessionMocks.createSessionDto(1L, null, testTeacher.getId(), Arrays.asList(testUser), false, false);

        // Autre session pour tester les listes
        Session testSession2 = sessionMocks.createSession(2L, testTeacher, Collections.emptyList(), false, false);
        SessionDto testSessionDto2 = sessionMocks.createSessionDto(2L, null, testTeacher.getId(), Collections.emptyList(), false, false);

        testSessions = Arrays.asList(testSession, testSession2);
        testSessionDtos = Arrays.asList(testSessionDto, testSessionDto2);

        // Définition du comportement par défaut des mocks
        when(sessionMapper.toDto(testSession)).thenReturn(testSessionDto);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(testSessionDto);
        when(sessionMapper.toDto(testSessions)).thenReturn(testSessionDtos);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(testSession);
    }

    // Test du endpoint GET /api/session/{id} - cas de succès
    @Test
    @DisplayName("GET /api/session/{id} - Success")
    public void testGetSessionById_Success() throws Exception {
        when(sessionService.getById(1L)).thenReturn(testSession);

        mockMvc.perform(get("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yoga session"))
                .andExpect(jsonPath("$.description").value("A relaxing yoga session"))
                .andExpect(jsonPath("$.teacher_id").value(1));

        verify(sessionService).getById(1L);
        verify(sessionMapper).toDto(testSession);
    }

    // Test GET /api/session/{id} - session non trouvée
    @Test
    @DisplayName("GET /api/session/{id} - Not Found")
    public void testGetSessionById_NotFound() throws Exception {
        when(sessionService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/session/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isNotFound());

        verify(sessionService).getById(99L);
        verify(sessionMapper, never()).toDto(any(Session.class));
    }

    // Test GET /api/session/{id} - ID invalide (non numérique)
    @Test
    @DisplayName("GET /api/session/{id} - Invalid ID")
    public void testGetSessionById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/session/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).getById(anyLong());
    }

    // Test GET /api/session - récupération de toutes les sessions
    @Test
    @DisplayName("GET /api/session - Success")
    public void testGetAllSessions_Success() throws Exception {
        when(sessionService.findAll()).thenReturn(testSessions);

        mockMvc.perform(get("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Yoga session"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Yoga session"));

        verify(sessionService).findAll();
        verify(sessionMapper).toDto(testSessions);
    }

    // Test GET /api/session - liste vide
    @Test
    @DisplayName("GET /api/session - Empty List")
    public void testGetAllSessions_EmptyList() throws Exception {
        when(sessionService.findAll()).thenReturn(Collections.emptyList());
        when(sessionMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(sessionService).findAll();
        verify(sessionMapper).toDto(Collections.emptyList());
    }

    // Test POST /api/session - création d’une nouvelle session
    @Test
    @DisplayName("POST /api/session - Success")
    public void testCreateSession_Success() throws Exception {
        when(sessionService.create(testSession)).thenReturn(testSession);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com"))
                        .content(objectMapper.writeValueAsString(testSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga session"));

        verify(sessionMapper).toEntity(any(SessionDto.class));
        verify(sessionService).create(any(Session.class));
        verify(sessionMapper).toDto(any(Session.class));
    }

    // Test PUT /api/session/{id} - mise à jour réussie
    @Test
    @DisplayName("PUT /api/session/{id} - Success")
    public void testUpdateSession_Success() throws Exception {
        when(sessionService.update(eq(1L), any(Session.class))).thenReturn(testSession);

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com"))
                        .content(objectMapper.writeValueAsString(testSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga session"));

        verify(sessionMapper).toEntity(any(SessionDto.class));
        verify(sessionService).update(eq(1L), any(Session.class));
        verify(sessionMapper).toDto(any(Session.class));
    }

    // Test PUT /api/session/{id} - ID invalide
    @Test
    @DisplayName("PUT /api/session/{id} - Invalid ID")
    public void testUpdateSession_InvalidId() throws Exception {
        mockMvc.perform(put("/api/session/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com"))
                        .content(objectMapper.writeValueAsString(testSessionDto)))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).update(anyLong(), any(Session.class));
    }

    // Test DELETE /api/session/{id} - suppression réussie
    @Test
    @DisplayName("DELETE /api/session/{id} - Success")
    public void testDeleteSession_Success() throws Exception {
        when(sessionService.getById(1L)).thenReturn(testSession);

        mockMvc.perform(delete("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isOk());

        verify(sessionService).getById(1L);
        verify(sessionService).delete(1L);
    }

    // Test DELETE /api/session/{id} - session non trouvée
    @Test
    @DisplayName("DELETE /api/session/{id} - Not Found")
    public void testDeleteSession_NotFound() throws Exception {
        when(sessionService.getById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isNotFound());

        verify(sessionService).getById(99L);
        verify(sessionService, never()).delete(anyLong());
    }

    // Test DELETE /api/session/{id} - ID invalide
    @Test
    @DisplayName("DELETE /api/session/{id} - Invalid ID")
    public void testDeleteSession_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/session/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).delete(anyLong());
    }

    // Test POST /api/session/{id}/participate/{userId} - participation réussie
    @Test
    @DisplayName("POST /api/session/{id}/participate/{userId} - Success")
    public void testParticipate_Success() throws Exception {
        doNothing().when(sessionService).participate(1L, 1L);

        mockMvc.perform(post("/api/session/1/participate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isOk());

        verify(sessionService).participate(1L, 1L);
    }

    // Test POST /api/session/{id}/participate/{userId} - IDs invalides
    @Test
    @DisplayName("POST /api/session/{id}/participate/{userId} - Invalid IDs")
    public void testParticipate_InvalidIds() throws Exception {
        mockMvc.perform(post("/api/session/invalid/participate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/session/1/participate/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).participate(anyLong(), anyLong());
    }

    // Test DELETE /api/session/{id}/participate/{userId} - désinscription réussie
    @Test
    @DisplayName("DELETE /api/session/{id}/participate/{userId} - Success")
    public void testNoLongerParticipate_Success() throws Exception {
        doNothing().when(sessionService).noLongerParticipate(1L, 1L);

        mockMvc.perform(delete("/api/session/1/participate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isOk());

        verify(sessionService).noLongerParticipate(1L, 1L);
    }

    // Test DELETE /api/session/{id}/participate/{userId} - IDs invalides
    @Test
    @DisplayName("DELETE /api/session/{id}/participate/{userId} - Invalid IDs")
    public void testNoLongerParticipate_InvalidIds() throws Exception {
        mockMvc.perform(delete("/api/session/invalid/participate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/session/1/participate/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("andre@mail.com")))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}