package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Service de test pour la gestion des sessions
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    // Mock de données
    private Session session;
    private User user;
    private final Long SESSION_ID = 1L;
    private final Long USER_ID = 1L;

    // Initialisation des objets avant chaque test
    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(SESSION_ID);
        session.setUsers(new ArrayList<>());

        user = new User();
        user.setId(USER_ID);
    }

    // Tests pour la méthode create
    @Test
    @DisplayName("Should create a session")
    void create_ShouldReturnCreatedSession() {
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session result = sessionService.create(session);

        assertNotNull(result);
        assertEquals(SESSION_ID, result.getId());
        verify(sessionRepository, times(1)).save(session);
    }

    // Tests pour la méthode delete
    @Test
    @DisplayName("Should delete a session")
    void delete_ShouldCallRepositoryDeleteById() {
        sessionService.delete(SESSION_ID);
        verify(sessionRepository, times(1)).deleteById(SESSION_ID);
    }

    // Tests pour la méthode findAll
    @Test
    @DisplayName("Should return all sessions")
    void findAll_ShouldReturnAllSessions() {
        Session session2 = new Session();
        session2.setId(2L);
        List<Session> sessions = Arrays.asList(session, session2);

        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(sessions, result);
        verify(sessionRepository, times(1)).findAll();
    }

    // Tests pour la méthode getById
    @Test
    @DisplayName("Should return session when it exists")
    void getById_ShouldReturnSession_WhenSessionExists() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(SESSION_ID);

        assertNotNull(result);
        assertEquals(SESSION_ID, result.getId());
        verify(sessionRepository, times(1)).findById(SESSION_ID);
    }

    // Tests pour la méthode getById
    @Test
    @DisplayName("Should return null when session doesn't exist")
    void getById_ShouldReturnNull_WhenSessionDoesNotExist() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        Session result = sessionService.getById(SESSION_ID);

        assertNull(result);
        verify(sessionRepository, times(1)).findById(SESSION_ID);
    }

    // Tests pour la méthode update
    @Test
    @DisplayName("Should update session")
    void update_ShouldReturnUpdatedSession() {
        Session updatedSession = new Session();
        updatedSession.setId(2L);

        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);

        Session result = sessionService.update(SESSION_ID, updatedSession);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(updatedSession);
        assertEquals(SESSION_ID, updatedSession.getId());
    }

    // Tests pour la méthode participate
    @Test
    @DisplayName("Should add user to session participants")
    void participate_ShouldAddUserToSession_WhenUserNotAlreadyParticipating() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        sessionService.participate(SESSION_ID, USER_ID);

        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(sessionRepository, times(1)).save(session);
    }

    // Tests pour les cas d'erreur dans la méthode participate
    @Test
    @DisplayName("Should throw NotFoundException when session doesn't exist")
    void participate_ShouldThrowNotFoundException_WhenSessionDoesNotExist() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () ->
            sessionService.participate(SESSION_ID, USER_ID)
        );

        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(sessionRepository, never()).save(any(Session.class));
    }
    
    // Tests pour les cas d'erreur dans la méthode participate
    @Test
    @DisplayName("Should throw NotFoundException when user doesn't exist")
    void participate_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            sessionService.participate(SESSION_ID, USER_ID)
        );

        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    // Tests pour les cas d'erreur dans la méthode participate
    @Test
    @DisplayName("Should throw BadRequestException when user already participates")
    void participate_ShouldThrowBadRequestException_WhenUserAlreadyParticipates() {
        session.getUsers().add(user);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () ->
            sessionService.participate(SESSION_ID, USER_ID)
        );

        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    // Tests pour la méthode noLongerParticipate
    @Test
    @DisplayName("Should remove user from session participants")
    void noLongerParticipate_ShouldRemoveUserFromSession_WhenUserIsParticipating() {
        session.getUsers().add(user);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(SESSION_ID, USER_ID);

        assertFalse(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(sessionRepository, times(1)).save(session);
    }

    // Tests pour les cas d'erreur dans la méthode noLongerParticipate
    @Test
    @DisplayName("Should throw NotFoundException when session doesn't exist")
    void noLongerParticipate_ShouldThrowNotFoundException_WhenSessionDoesNotExist() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            sessionService.noLongerParticipate(SESSION_ID, USER_ID)
        );

        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when user is not participating")
    void noLongerParticipate_ShouldThrowBadRequestException_WhenUserIsNotParticipating() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () ->
            sessionService.noLongerParticipate(SESSION_ID, USER_ID)
        );

        verify(sessionRepository, times(1)).findById(SESSION_ID);
        verify(sessionRepository, never()).save(any(Session.class));
    }
}
