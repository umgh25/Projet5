package com.openclassrooms.starterjwt.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.mocks.SessionMocks;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;

// Intégration test pour la couche repository de Session
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class SessionRepositoryTest {

    private final SessionRepository sessionRepository;
    private final TeacherRepository teacherRepository;
    private final SessionMocks sessionMocks = new SessionMocks();

    @Autowired
    public SessionRepositoryTest(SessionRepository sessionRepository,  TeacherRepository teacherRepository) {
        this.sessionRepository = sessionRepository;
        this.teacherRepository = teacherRepository;
    }

    private Session session;
    Session savedSessionInDB;
    // Initialisation avant chaque test
    @BeforeEach
    public void setup() {
        List<Teacher> teachers = teacherRepository.findAll();
        assertFalse(teachers.isEmpty());
        Teacher teacher = teachers.get(0);
        session = sessionMocks.createSession(null, teacher, null, false, false);
        savedSessionInDB = sessionRepository.save(session);
    }
    // Tests
    @Test
    @DisplayName("Save a session")
    public void shouldSaveSession() {
        assertNotNull(savedSessionInDB.getId());
        assertEquals(session.getName(), savedSessionInDB.getName());
        assertEquals(session.getDescription(), savedSessionInDB.getDescription());
        assertEquals(session.getTeacher().getFirstName(), savedSessionInDB.getTeacher().getFirstName());
        assertEquals(session.getTeacher().getLastName(), savedSessionInDB.getTeacher().getLastName());
    }
    // Retrouve toutes les sessions
    @Test
    @DisplayName("Find all sessions")
    public void shouldFindAllSessions() {
        sessionRepository.deleteAll();
        List<Session> sessions = sessionRepository.findAll();
        assertTrue(sessions.isEmpty());
    }
    // Retrouve une session par son id
    @Test
    @DisplayName("Find session by id")
    public void shouldFindSessionById() {
        Session foundSession = sessionRepository.findById(savedSessionInDB.getId()).orElse(null);
        assertNotNull(foundSession);
        assertEquals(savedSessionInDB.getId(), foundSession.getId());
        assertEquals(savedSessionInDB.getName(), foundSession.getName());
        assertEquals(savedSessionInDB.getDescription(), foundSession.getDescription());
        assertEquals(savedSessionInDB.getTeacher().getFirstName(), foundSession.getTeacher().getFirstName());
        assertEquals(savedSessionInDB.getTeacher().getLastName(), foundSession.getTeacher().getLastName());
    }
    // Supprime une session
    @Test
    @DisplayName("Delete session")
    public void shouldDeleteSession() {
        sessionRepository.deleteById(savedSessionInDB.getId());
        assertFalse(sessionRepository.findById(savedSessionInDB.getId()).isPresent());
    }
    // Met à jour une session
    @Test
    @DisplayName("Update session")
    public void shouldUpdateSession() {
        savedSessionInDB.setName("Session de Yoga pour les débutants");
        savedSessionInDB.setDescription("Session de yoga pour les débutants avec des exercices simples");
        savedSessionInDB.setDate(Date.valueOf(LocalDate.of(2025, 3, 11)));
        Session updatedSessionInDB = sessionRepository.save(savedSessionInDB);
        assertEquals(savedSessionInDB.getId(), updatedSessionInDB.getId());
        assertEquals(savedSessionInDB.getName(), updatedSessionInDB.getName());
        assertEquals(savedSessionInDB.getDescription(), updatedSessionInDB.getDescription());
        assertEquals(savedSessionInDB.getDate(), updatedSessionInDB.getDate());
    }
}