package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mocks.SessionMocks;
import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Test unitaires de la classe SessionMapper
@ExtendWith(MockitoExtension.class)
public class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionMapper sessionMapper;

    private final SessionMocks sessionMocks = new SessionMocks();
    private final UserMocks userMocks = new UserMocks();
    private final TeacherMocks teacherMocks = new TeacherMocks();

    private Teacher teacher;
    private User firstUser, secondUser;
    private List<User> users;
    // Initialisation des objets de test avant chaque test
    @BeforeEach
    public void setUp() {
        teacher = teacherMocks.createTeacher(1L, "Test", "John", false);
        firstUser = userMocks.createUser(1L, "jean@mail.com", "Test", "Jean", "password", false, false);
        secondUser = userMocks.createUser(2L, "paul@mail.com", "Test", "Paul", "password", false, false);
        users = Arrays.asList(firstUser, secondUser);
    }
    // Test de la méthode toDto
    @Test
    @DisplayName("should map Session entity to SessionDto")
    public void shouldMapSessionEntityToSessionDto() {
        Session session = sessionMocks.createSession(null, teacher, users, false, false);

        SessionDto dto = sessionMapper.toDto(session);

        assertNotNull(dto);
        assertEquals(session.getName(), dto.getName());
        assertEquals(session.getDescription(), dto.getDescription());
        assertEquals(teacher.getId(), dto.getTeacher_id());
        assertEquals(2, dto.getUsers().size());
        assertTrue(dto.getUsers().contains(firstUser.getId()));
    }
    // Test de la méthode toEntity
    @Test
    @DisplayName("should map SessionDto to Session entity")
    public void shouldMapSessionDtoToSessionEntity() {
        SessionDto dto = sessionMocks.createSessionDto(null, null, teacher.getId(), users, false, false);

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(firstUser);
        when(userService.findById(2L)).thenReturn(secondUser);

        Session session = sessionMapper.toEntity(dto);

        assertNotNull(session);
        assertEquals(dto.getName(), session.getName());
        assertNotNull(session.getTeacher());
        assertEquals(teacher.getId(), session.getTeacher().getId());
        assertEquals(2, session.getUsers().size());
    }
    // Tests des cas limites
    @Test
    @DisplayName("should handle null teacher in mapping")
    public void shouldHandleNullTeacherInDtoToEntity() {
        SessionDto dto = sessionMocks.createSessionDto(null, null, null, users, true, false);

        Session session = sessionMapper.toEntity(dto);

        assertNotNull(session);
        assertNull(session.getTeacher());
    }
    // Test de la gestion d'une liste d'utilisateurs vide
    @Test
    @DisplayName("should handle empty user list")
    public void shouldHandleEmptyUserListInMapping() {
        SessionDto dto = sessionMocks.createSessionDto(null, null, teacher.getId(), List.of(), false, false);
        when(teacherService.findById(teacher.getId())).thenReturn(teacher);

        Session session = sessionMapper.toEntity(dto);

        assertNotNull(session);
        assertTrue(session.getUsers().isEmpty());
    }
}