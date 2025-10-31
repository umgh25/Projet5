package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mocks.TeacherMocks;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

// Test unitaires de la classe TeacherMapper
@ExtendWith(MockitoExtension.class)
public class TeacherMapperTest {

    @InjectMocks
    private TeacherMapper teacherMapper;

    private final TeacherMocks teacherMocks = new TeacherMocks();
    // Test de la méthode toDto
    @Test
    @DisplayName("should map Teacher entity to TeacherDto")
    public void shouldMapTeacherEntityToTeacherDto() {
        Teacher teacher = teacherMocks.createTeacher(1L, "Test", "John", false);

        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        assertNotNull(teacherDto);
        assertEquals(teacher.getId(), teacherDto.getId());
        assertEquals(teacher.getLastName(), teacherDto.getLastName());
        assertEquals(teacher.getFirstName(), teacherDto.getFirstName());
        assertEquals(teacher.getCreatedAt(), teacherDto.getCreatedAt());
        assertEquals(teacher.getUpdatedAt(), teacherDto.getUpdatedAt());
    }
    // Test de la méthode toEntity
    @Test
    @DisplayName("should map TeacherDto to Teacher entity")
    public void shouldMapTeacherDtoToTeacherEntity() {
        TeacherDto teacherDto = teacherMocks.createTeacherDto(1L, "Test", "Jane", false);

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertNotNull(teacher);
        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
        assertEquals(teacherDto.getCreatedAt(), teacher.getCreatedAt());
        assertEquals(teacherDto.getUpdatedAt(), teacher.getUpdatedAt());
    }
    // Test de la méthode toDto pour une liste
    @Test
    @DisplayName("should map list of Teacher entities to list of TeacherDtos")
    public void shouldMapTeacherListToDtoList() {
        List<Teacher> teachers = Arrays.asList(
                teacherMocks.createTeacher(1L, "Test", "Mike", false),
                teacherMocks.createTeacher(2L, "Test", "Sarah", false)
        );

        List<TeacherDto> teacherDtos = teacherMapper.toDto(teachers);

        assertNotNull(teacherDtos);
        assertEquals(2, teacherDtos.size());
        assertEquals(teachers.get(0).getId(), teacherDtos.get(0).getId());
        assertEquals(teachers.get(0).getFirstName(), teacherDtos.get(0).getFirstName());
        assertEquals(teachers.get(1).getId(), teacherDtos.get(1).getId());
        assertEquals(teachers.get(1).getFirstName(), teacherDtos.get(1).getFirstName());
    }
    // Test de la méthode toEntity pour une liste
    @Test
    @DisplayName("should map list of TeacherDtos to list of Teacher entities")
    public void shouldMapDtoListToTeacherList() {
        List<TeacherDto> teacherDtos = Arrays.asList(
                teacherMocks.createTeacherDto(1L, "Brown", "Robert", false),
                teacherMocks.createTeacherDto(2L, "Davis", "Emily", false)
        );

        List<Teacher> teachers = teacherMapper.toEntity(teacherDtos);

        assertNotNull(teachers);
        assertEquals(2, teachers.size());
        assertEquals(teacherDtos.get(0).getId(), teachers.get(0).getId());
        assertEquals(teacherDtos.get(0).getFirstName(), teachers.get(0).getFirstName());
        assertEquals(teacherDtos.get(1).getId(), teachers.get(1).getId());
        assertEquals(teacherDtos.get(1).getFirstName(), teachers.get(1).getFirstName());
    }
    // Tests des cas limites pour les valeurs longues
    @Test
    @DisplayName("should handle long name values in Teacher → TeacherDto mapping")
    public void shouldHandleLongValuesInTeacherToDto() {
        Teacher teacher = teacherMocks.createTeacher(1L, "A".repeat(30), "B".repeat(30), true);

        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        assertNotNull(teacherDto);
        assertEquals(teacher.getId(), teacherDto.getId());
        assertEquals(teacher.getLastName(), teacherDto.getLastName());
        assertEquals(teacher.getFirstName(), teacherDto.getFirstName());
    }
    // Tests des cas limites pour les valeurs longues
    @Test
    @DisplayName("should handle long name values in TeacherDto → Teacher mapping")
    public void shouldHandleLongValuesInDtoToTeacher() {
        TeacherDto teacherDto = teacherMocks.createTeacherDto(1L, "C".repeat(30), "D".repeat(30), true);

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertNotNull(teacher);
        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
    }
}
