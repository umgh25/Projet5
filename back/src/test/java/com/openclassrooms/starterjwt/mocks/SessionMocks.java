package com.openclassrooms.starterjwt.mocks;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;

// Utility class to create mock Session and SessionDto objects for testing
public class SessionMocks {
    private Date sessionDate = Date.valueOf("2025-03-10");
    // Predefined mock users
    public Session createSession(Long id, Teacher teacher, List<User> users, Boolean nullValues, Boolean longValues) {
        String longName = "Yoga".repeat(50);
        String longDescription = "Yoga session for beginners".repeat(1000);

        return Session.builder()
                .id(id != null ? id : 1L)
                .name(longValues ? longName : "Yoga session")
                .description(longValues ? longDescription : "A relaxing yoga session")
                .date(sessionDate)
                .teacher(nullValues ? null : teacher)
                .users(nullValues ? null : users)
                .build();
    }
    // Method to create a mock SessionDto object
    public SessionDto createSessionDto(Long id, String name, Long teacherId, List<User> users, Boolean nullValues,
            Boolean longValues) {
        String longName = "Yoga".repeat(50);
        String longDescription = "Yoga session for beginners".repeat(1000);
        SessionDto newSessionDto = new SessionDto();

        List<Long> userIds = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
        }
        // Set fields with provided or default values
        newSessionDto.setId(id != null ? id : 3L);
        newSessionDto.setName(longValues ? longName : name != null ? name : "Yoga session");
        newSessionDto.setDescription(longValues ? longDescription : "A relaxing yoga session");
        newSessionDto.setDate(sessionDate);
        newSessionDto.setTeacher_id(nullValues ? null : teacherId);
        newSessionDto.setUsers(nullValues ? null : userIds);
        return newSessionDto;
    }
}