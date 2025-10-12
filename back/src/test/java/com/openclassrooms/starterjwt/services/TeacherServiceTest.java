package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Test pour la classe TeacherService
@SpringBootTest
public class TeacherServiceTest {

    private TeacherService teacherService;
    private TeacherMocks teacherMocks = new TeacherMocks();

    @MockBean
    private TeacherRepository teacherRepository;

    @Autowired
    public TeacherServiceTest(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    private Teacher teacher;
    private final Long TEACHER_ID = 1L;
    // Initialisation avant chaque test
    @BeforeEach
    void setUp() {
        teacher = teacherMocks.createTeacher(TEACHER_ID, "Teacher", "Margot", false);
    }
    // Tests pour la méthode findAll
    @Nested
    @DisplayName("Tests for findAll method")
    class FindAllTests {
        @Test
        @DisplayName("Should return all teachers")
        void findAll_ShouldReturnAllTeachers() {
            Teacher teacher2 = teacherMocks.createTeacher(2L, "Teacher", "John", false);

            List<Teacher> teachers = Arrays.asList(teacher, teacher2);
            when(teacherRepository.findAll()).thenReturn(teachers);
            List<Teacher> result = teacherService.findAll();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(teachers, result);
            verify(teacherRepository, times(1)).findAll();
        }
        // Tests pour les cas d'erreur dans la méthode findAll
        @Test
        @DisplayName("Should return empty list when no teachers exist")
        void findAll_ShouldReturnEmptyList_WhenNoTeachersExist() {
            when(teacherRepository.findAll()).thenReturn(Arrays.asList());
            List<Teacher> result = teacherService.findAll();
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(teacherRepository, times(1)).findAll();
        }
    }
    // Tests pour la méthode findById
    @Nested
    @DisplayName("Tests for findById method")
    class FindByIdTests {
        @Test
        @DisplayName("Should return teacher when it exists")
        void findById_ShouldReturnTeacher_WhenTeacherExists() {
            when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(teacher));
            Teacher result = teacherService.findById(TEACHER_ID);

            assertNotNull(result);
            assertEquals(TEACHER_ID, result.getId());
            assertEquals("Teacher", result.getLastName());
            assertEquals("Margot", result.getFirstName());
            verify(teacherRepository, times(1)).findById(TEACHER_ID);
        }
        // Tests pour les cas d'erreur dans la méthode findById
        @Test
        @DisplayName("Should return null when teacher doesn't exist")
        void findById_ShouldReturnNull_WhenTeacherDoesNotExist() {
            when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.empty());
            Teacher result = teacherService.findById(TEACHER_ID);
            assertNull(result);
            verify(teacherRepository, times(1)).findById(TEACHER_ID);
        }
    }
}