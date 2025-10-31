package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

// Tests unitaires de la classe UserMapper
@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    private final UserMocks userMocks = new UserMocks();
    // Test de la méthode toDto
    @Test
    @DisplayName("should map User entity to UserDto")
    public void shouldMapUserEntityToUserDto() {
        User user = userMocks.createUser(
                1L, "Margot.test@mail.com", "Test", "Margot", "password123", true, false
        );

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.isAdmin(), userDto.isAdmin());
        assertEquals(user.getCreatedAt(), userDto.getCreatedAt());
        assertEquals(user.getUpdatedAt(), userDto.getUpdatedAt());
    }
    // Test de la méthode toEntity
    @Test
    @DisplayName("should map UserDto to User entity")
    public void shouldMapUserDtoToUserEntity() {
        UserDto userDto = userMocks.createUserDto(
                2L, "paul.test@mail.com", "Test", "Paul", "azerty123", false, false
        );

        User user = userMapper.toEntity(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.isAdmin(), user.isAdmin());
        assertEquals(userDto.getCreatedAt(), user.getCreatedAt());
        assertEquals(userDto.getUpdatedAt(), user.getUpdatedAt());
    }
    // Test de la méthode toDto pour une liste
    @Test
    @DisplayName("should map list of User entities to list of UserDtos")
    public void shouldMapUserListToDtoList() {
        List<User> users = Arrays.asList(
                userMocks.createUser(3L, "alice@mail.com", "Test", "Alice", "alicePass", true, false),
                userMocks.createUser(4L, "bob@mail.com", "Test", "Bob", "bobPass", false, false)
        );

        List<UserDto> userDtos = userMapper.toDto(users);

        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());

        // User 1
        assertEquals(users.get(0).getId(), userDtos.get(0).getId());
        assertEquals(users.get(0).getEmail(), userDtos.get(0).getEmail());
        assertTrue(userDtos.get(0).isAdmin());

        // User 2
        assertEquals(users.get(1).getId(), userDtos.get(1).getId());
        assertEquals(users.get(1).getEmail(), userDtos.get(1).getEmail());
        assertFalse(userDtos.get(1).isAdmin());
    }
    // Test de la méthode toEntity pour une liste
    @Test
    @DisplayName("should map list of UserDtos to list of User entities")
    public void shouldMapDtoListToUserList() {
        List<UserDto> userDtos = Arrays.asList(
                userMocks.createUserDto(5L, "charlie@mail.com", "Test", "Charlie", "charliePass", true, false),
                userMocks.createUserDto(6L, "diana@mail.com", "Test", "Diana", "dianaPass", false, false)
        );

        List<User> users = userMapper.toEntity(userDtos);

        assertNotNull(users);
        assertEquals(2, users.size());

        // User 1
        assertEquals(userDtos.get(0).getId(), users.get(0).getId());
        assertEquals(userDtos.get(0).getEmail(), users.get(0).getEmail());
        assertTrue(users.get(0).isAdmin());

        // User 2
        assertEquals(userDtos.get(1).getId(), users.get(1).getId());
        assertEquals(userDtos.get(1).getEmail(), users.get(1).getEmail());
        assertFalse(users.get(1).isAdmin());
    }
    // Tests des cas limites pour les valeurs longues
    @Test
    @DisplayName("hould handle long values in User → UserDto mapping")
    public void shouldHandleLongValuesInUserToDto() {
        User user = userMocks.createUser(
                7L, "verylong.email.address@verylongdomainmail.com",
                "A".repeat(30), "B".repeat(30), "C".repeat(150),
                true, true
        );

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.isAdmin(), userDto.isAdmin());
    }
    // Tests des cas limites pour les valeurs longues
    @Test
    @DisplayName("should handle long values in UserDto → User mapping")
    public void shouldHandleLongValuesInDtoToUser() {
        UserDto userDto = userMocks.createUserDto(
                8L, "another.very.long.email@extremelylongdomain.com",
                "D".repeat(30), "E".repeat(30), "F".repeat(150),
                false, true
        );

        User user = userMapper.toEntity(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.isAdmin(), user.isAdmin());
    }
}
