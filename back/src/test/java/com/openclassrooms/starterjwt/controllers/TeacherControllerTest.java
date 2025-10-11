package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

// Test unitaire pour le TeacherController en utilisant MockMvc
@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerTest {

    private MockMvc mockMvc;
    
    @Autowired
    public TeacherControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    private Teacher testTeacher;
    private TeacherDto testTeacherDto;
    private List<Teacher> testTeachers;
    private List<TeacherDto> testTeacherDtos;
    // Initialisation des données de test avant chaque test
    @BeforeEach
    public void setup() {
        testTeacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTeacherDto = new TeacherDto();
        testTeacherDto.setId(1L);
        testTeacherDto.setFirstName("John");
        testTeacherDto.setLastName("Doe");
        testTeacherDto.setCreatedAt(testTeacher.getCreatedAt());
        testTeacherDto.setUpdatedAt(testTeacher.getUpdatedAt());

        Teacher testTeacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTeachers = Arrays.asList(testTeacher, testTeacher2);

        TeacherDto testTeacherDto2 = new TeacherDto();
        testTeacherDto2.setId(2L);
        testTeacherDto2.setFirstName("Jane");
        testTeacherDto2.setLastName("Smith");
        testTeacherDto2.setCreatedAt(testTeacher2.getCreatedAt());
        testTeacherDto2.setUpdatedAt(testTeacher2.getUpdatedAt());

        testTeacherDtos = Arrays.asList(testTeacherDto, testTeacherDto2);

        when(teacherMapper.toDto(testTeacher)).thenReturn(testTeacherDto);
        when(teacherMapper.toDto(testTeachers)).thenReturn(testTeacherDtos);
    }
    // Test pour la récupération d'un enseignant par ID
    @Test
    @DisplayName("GET /api/teacher/{id} - Success")
    public void testGetTeacherById_Success() throws Exception {
        when(teacherService.findById(1L)).thenReturn(testTeacher);

        mockMvc.perform(get("/api/teacher/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(teacherService).findById(1L);
        verify(teacherMapper).toDto(testTeacher);
    }
    // Test pour le cas où l'enseignant n'est pas trouvé
    @Test
    @DisplayName("GET /api/teacher/{id} - Not Found")
    public void testGetTeacherById_NotFound() throws Exception {
        when(teacherService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/99")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isNotFound());

        verify(teacherService).findById(99L);
        verify(teacherMapper, never()).toDto(any(Teacher.class));
    }
    // Test pour le cas où l'ID fourni est invalide
    @Test
    @DisplayName("GET /api/teacher/{id} - Invalid ID")
    public void testGetTeacherById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/teacher/invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isBadRequest());

        verify(teacherService, never()).findById(any());
    }
    // Test pour la récupération de tous les enseignants
    @Test
    @DisplayName("GET /api/teacher - Success")
    public void testGetAllTeachers_Success() throws Exception {
        when(teacherService.findAll()).thenReturn(testTeachers);

        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));

        verify(teacherService).findAll();
        verify(teacherMapper).toDto(testTeachers);
    }
    // Test pour le cas où la liste des enseignants est vide
    @Test
    @DisplayName("GET /api/teacher - Empty List")
    public void testGetAllTeachers_EmptyList() throws Exception {
        when(teacherService.findAll()).thenReturn(Collections.emptyList());
        when(teacherMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(teacherService).findAll();
        verify(teacherMapper).toDto(Collections.emptyList());
    }
}