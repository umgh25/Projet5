package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mocks.SessionMocks;
import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@SpringBootTest
public class SessionMapperTest {

    private final SessionMapper sessionMapper;
    private final SessionMocks sessionMocks = new SessionMocks();
    private final UserMocks userMocks = new UserMocks();
    private final TeacherMocks teacherMocks = new TeacherMocks();
    // Constructeur avec injection de dépendances
    @Autowired
    public SessionMapperTest(SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }
    // Mock des services nécessaires pour le mappage
    @MockBean
    private TeacherService teacherService;
    // Mock des services nécessaires pour le mappage
    @MockBean
    private UserService userService;
    // Données de test réutilisables
    private Teacher teacher;
    private User firstUser, secondUser;
    private List<User> users;
    // Initialisation des données de test avant chaque test
    @BeforeEach
    public void setUp() {
        teacher = teacherMocks.createTeacher(1L, "Test", "John", false);
        firstUser = userMocks.createUser(1L, "jean_test@mail.com", "Test", "Jean", "password", false, false);
        secondUser = userMocks.createUser(2L, "paul_test@mail.com", "Test", "Paul", "password", false, false);
        users = Arrays.asList(firstUser, secondUser);
    }
    // Tests de mappage
    @Test
    @DisplayName("Map Session entity to SessionDto")
    public void shouldMapSessionEntityToSessionDto() {
        Session session = sessionMocks.createSession(null, teacher, users, false, false);
        SessionDto sessionDto = sessionMapper.toDto(session);

        assertNotNull(sessionDto);
        assertEquals(session.getId(), sessionDto.getId());
        assertEquals(session.getName(), sessionDto.getName());
        assertEquals(session.getDescription(), sessionDto.getDescription());
        assertEquals(session.getTeacher().getId(), sessionDto.getTeacher_id());
        assertEquals(session.getUsers().get(0).getId(), sessionDto.getUsers().get(0));
        assertEquals(session.getUsers().get(1).getId(), sessionDto.getUsers().get(1));
        assertNotNull(sessionDto.getUsers());
        assertEquals(2, sessionDto.getUsers().size());
        assertTrue(sessionDto.getUsers().contains(session.getUsers().get(0).getId()));
        assertTrue(sessionDto.getUsers().contains(session.getUsers().get(1).getId()));
    }
    // Test de mappage de SessionDto à Session
    @Test
    @DisplayName("Map SessionDto to Session entity")
    public void shouldMapDtoToEntity() {
        SessionDto sessionDto = sessionMocks.createSessionDto(null, null, teacher.getId(), users, false, false);

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(firstUser);
        when(userService.findById(2L)).thenReturn(secondUser);

        Session session = sessionMapper.toEntity(sessionDto);

        assertNotNull(session);
        assertEquals(sessionDto.getId(), session.getId());
        assertEquals(sessionDto.getName(), session.getName());
        assertEquals(sessionDto.getDescription(), session.getDescription());
        assertNotNull(session.getTeacher());
        assertEquals(teacher.getId(), session.getTeacher().getId());
        assertNotNull(session.getUsers());
        assertEquals(2, session.getUsers().size());
        assertEquals(firstUser.getId(), session.getUsers().get(0).getId());
        assertEquals(secondUser.getId(), session.getUsers().get(1).getId());
    }
    // Tests de mappage de listes
    @Test
    @DisplayName("Map list of Session entities to list of SessionDtos")
    public void shouldMapSessionListToDtoList() {
        List<Session> sessions = Arrays.asList(
            sessionMocks.createSession(null, teacher, users, false, false),
            sessionMocks.createSession(null, teacher, users, true, false)
        );

        List<SessionDto> sessionDtos = sessionMapper.toDto(sessions);

        assertNotNull(sessionDtos);
        assertEquals(2, sessionDtos.size());
        assertEquals(sessions.get(0).getId(), sessionDtos.get(0).getId());
        assertEquals(sessions.get(0).getName(), sessionDtos.get(0).getName());
        assertEquals(sessions.get(0).getDescription(), sessionDtos.get(0).getDescription());
        assertEquals(sessions.get(0).getTeacher().getId(), sessionDtos.get(0).getTeacher_id());
        assertEquals(sessions.get(0).getUsers().get(0).getId(), sessionDtos.get(0).getUsers().get(0));
        assertEquals(sessions.get(0).getUsers().get(1).getId(), sessionDtos.get(0).getUsers().get(1));
        assertEquals(sessions.get(1).getId(), sessionDtos.get(1).getId());
        assertEquals(sessions.get(1).getName(), sessionDtos.get(1).getName());
        assertEquals(sessions.get(1).getDescription(), sessionDtos.get(1).getDescription());
        assertNull(sessionDtos.get(1).getTeacher_id());
        assertNotNull(sessionDtos.get(1).getUsers());
        assertTrue(sessionDtos.get(1).getUsers().isEmpty());
    }
    // Test de mappage de SessionDtos à Session
    @Test
    @DisplayName("Map list of SessionDtos to list of Session entities")
    public void shouldMapDtoListToSessionList() {
        List<SessionDto> sessionDtos = Arrays.asList(
            sessionMocks.createSessionDto(null, null, teacher.getId(), users, false, false),
            sessionMocks.createSessionDto(null, null, teacher.getId(), users, true, false)
        );

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(firstUser);
        when(userService.findById(2L)).thenReturn(secondUser);

        List<Session> sessions = sessionMapper.toEntity(sessionDtos);

        assertNotNull(sessions);
        assertEquals(2, sessions.size());
        assertEquals(sessionDtos.get(0).getId(), sessions.get(0).getId());
        assertEquals(sessionDtos.get(0).getName(), sessions.get(0).getName());
        assertEquals(sessionDtos.get(0).getDescription(), sessions.get(0).getDescription());
        assertNotNull(sessions.get(0).getTeacher());
        assertEquals(teacher.getId(), sessions.get(0).getTeacher().getId());
        assertNotNull(sessions.get(0).getUsers());
        assertEquals(2, sessions.get(0).getUsers().size());
        assertEquals(firstUser.getId(), sessions.get(0).getUsers().get(0).getId());
        assertEquals(secondUser.getId(), sessions.get(0).getUsers().get(1).getId());
        assertEquals(sessionDtos.get(1).getId(), sessions.get(1).getId());
        assertEquals(sessionDtos.get(1).getName(), sessions.get(1).getName());
        assertEquals(sessionDtos.get(1).getDescription(), sessions.get(1).getDescription());
        assertNull(sessions.get(1).getTeacher());
        assertNotNull(sessions.get(1).getUsers());
        assertTrue(sessions.get(1).getUsers().isEmpty());
    }
    // Tests pour les valeurs nulles
    @Test
    @DisplayName("Handle null values in Session to SessionDto mapping")
    public void shouldHandleNullValuesInSessionToDto() {
        Session session = sessionMocks.createSession(null, teacher, users, true, false);
        SessionDto dto = sessionMapper.toDto(session);

        assertNotNull(dto);
        assertEquals(session.getId(), dto.getId());
        assertEquals(session.getName(), dto.getName());
        assertEquals(session.getDescription(), dto.getDescription());
        assertNull(dto.getTeacher_id());
        assertNotNull(dto.getUsers());
        assertTrue(dto.getUsers().isEmpty());
    }
    // Test de mappage de SessionDto à Session
    @Test
    @DisplayName("Handle null values in SessionDto to Session mapping")
    public void shouldHandleNullValuesInDtoToEntity() {
        SessionDto dto = sessionMocks.createSessionDto(null, null, teacher.getId(), users, true, false);
        Session session = sessionMapper.toEntity(dto);

        assertNotNull(session);
        assertEquals(dto.getId(), session.getId());
        assertEquals(dto.getName(), session.getName());
        assertEquals(dto.getDescription(), session.getDescription());
        assertNull(session.getTeacher());
        assertNotNull(session.getUsers());
        assertTrue(session.getUsers().isEmpty());
    }
    // Tests pour les valeurs longues
    @Test
    @DisplayName("Handle long values in Session to SessionDto mapping")
    public void shouldHandleLongValuesInSessionToDto() {
        Session session = sessionMocks.createSession(null, teacher, users, false, true);
        SessionDto dto = sessionMapper.toDto(session);

        assertNotNull(dto);
        assertEquals(session.getId(), dto.getId());
        assertEquals(session.getName(), dto.getName());
        assertEquals(session.getDescription(), dto.getDescription());
        assertEquals(session.getTeacher().getId(), dto.getTeacher_id());
        assertEquals(session.getUsers().get(0).getId(), dto.getUsers().get(0));
        assertEquals(session.getUsers().get(1).getId(), dto.getUsers().get(1));
    }
    // Test de mappage de SessionDto à Session
    @Test
    @DisplayName("Handle long values in SessionDto to Session mapping")
    public void shouldHandleLongValuesInDtoToEntity() {
        SessionDto dto = sessionMocks.createSessionDto(null, null, teacher.getId(), users, false, true);

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(firstUser);
        when(userService.findById(2L)).thenReturn(secondUser);

        Session session = sessionMapper.toEntity(dto);

        assertNotNull(session);
        assertEquals(dto.getId(), session.getId());
        assertEquals(dto.getName(), session.getName());
        assertEquals(dto.getDescription(), session.getDescription());
        assertNotNull(session.getTeacher());
        assertEquals(teacher.getId(), session.getTeacher().getId());
        assertNotNull(session.getUsers());
        assertEquals(2, session.getUsers().size());
        assertEquals(firstUser.getId(), session.getUsers().get(0).getId());
        assertEquals(secondUser.getId(), session.getUsers().get(1).getId());
    }
}