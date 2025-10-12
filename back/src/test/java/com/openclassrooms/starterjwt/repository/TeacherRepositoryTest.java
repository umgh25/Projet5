package com.openclassrooms.starterjwt.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.models.Teacher;

// Intégration test pour la couche repository de Teacher
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class TeacherRepositoryTest {
    private final TeacherRepository teacherRepository;
    private final TeacherMocks teacherMocks = new TeacherMocks();
    private Teacher teacher;
    // Constructeur avec injection de dépendances
    @Autowired
    public TeacherRepositoryTest(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }
    // Nettoyage avant chaque test
    @BeforeEach
    public void cleanUp() {
        teacher = teacherMocks.createTeacher(1L, "Teacher", "Margot", false);
    }
    // Tests
    @Test
    public void shouldSaveTeacher() {
        Teacher savedTeacherInDB = teacherRepository.save(teacher);
        assertNotNull(savedTeacherInDB.getId());
        assertEquals(teacher.getFirstName(), savedTeacherInDB.getFirstName());
        assertEquals(teacher.getLastName(), savedTeacherInDB.getLastName());
    }
    // Retrouve tous les enseignants
    @Test
    public void shouldFindAllTeachers() {
        assertNotNull(teacherRepository.findAll());
    }
    // Retrouve un enseignant par son id
    @Test
    public void shouldFindTeacherById() {
        Teacher savedTeacherInDB = teacherRepository.save(teacher);
        Teacher teacherInDB = teacherRepository.findById(savedTeacherInDB.getId()).orElse(null);
        assertNotNull(teacherInDB);
        assertEquals(teacher.getFirstName(), teacherInDB.getFirstName());
        assertEquals(teacher.getLastName(), teacherInDB.getLastName());
    }
    // Met à jour un enseignant
    @Test
    public void shouldDeleteTeacher() {
        Teacher savedTeacherInDB = teacherRepository.save(teacher);
        teacherRepository.deleteById(savedTeacherInDB.getId());
        Teacher teacherInDB = teacherRepository.findById(savedTeacherInDB.getId()).orElse(null);
        assertEquals(null, teacherInDB);
    }
}