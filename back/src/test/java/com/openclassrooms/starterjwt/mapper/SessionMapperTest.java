package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mocks.SessionMocks;
import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

public class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionMapperImpl sessionMapper;

    private final SessionMocks sessionMocks = new SessionMocks();
    private final UserMocks userMocks = new UserMocks();
    private final TeacherMocks teacherMocks = new TeacherMocks();

    private Teacher teacher;
    private User firstUser, secondUser;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // ✅ initialise les mocks

        teacher = teacherMocks.createTeacher(1L, "Test", "John", false);
        firstUser = userMocks.createUser(1L, "jean@mail.com", "Test", "Jean", "password", false, false);
        secondUser = userMocks.createUser(2L, "paul@mail.com", "Test", "Paul", "password", false, false);
        users = Arrays.asList(firstUser, secondUser);
    }

    @Test
    @DisplayName("Map SessionDto to Session entity")
    public void shouldMapDtoToEntity() {
        SessionDto sessionDto = sessionMocks.createSessionDto(null, null, teacher.getId(), users, false, false);

        when(teacherService.findById(teacher.getId())).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(firstUser);
        when(userService.findById(2L)).thenReturn(secondUser);

        Session session = sessionMapper.toEntity(sessionDto);

        assertNotNull(session);
        assertEquals(teacher.getId(), session.getTeacher().getId());
        assertEquals(2, session.getUsers().size());
    }
}
