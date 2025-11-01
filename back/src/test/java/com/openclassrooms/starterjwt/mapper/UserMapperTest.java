package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mocks.UserMocks;
import com.openclassrooms.starterjwt.models.User;


public class UserMapperTest {
    private final UserMapper userMapper = new UserMapperImpl();
    private final UserMocks userMocks = new UserMocks();
    // Constructeur avec injection de dépendances

    // Tests de mappage
    @Test
    @DisplayName("Map User entity to UserDto")
    public void shouldMapUserEntityToUserDto() {
        User user = userMocks.createUser(1L, "Margot.test@mail.com", "Test", "Margot", "password123", true, false);
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
    // Test de mappage de UserDto à User
    @Test
    @DisplayName("Map UserDto to User entity")
    public void shouldMapUserDtoToUserEntity() {
        UserDto userDto = userMocks.createUserDto(2L, "paul.test@mail.com", "Test", "Paul", "azerty123", false, false);

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
    // Test de mappage de UserDto à User
    @Test
    @DisplayName("Map list of User entities to list of UserDtos")
    public void shouldMapUserListToDtoList() {
        List<User> users = Arrays.asList(
            userMocks.createUser(3L, "alice@mail.com", "Test", "Alice", "alicePass", true, false),
            userMocks.createUser(4L, "bob@mail.com", "Test", "Bob", "bobPass", false, false)
        );

        List<UserDto> userDtos = userMapper.toDto(users);
        
        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());
        
        assertEquals(3L, userDtos.get(0).getId());
        assertEquals(users.get(0).getEmail(), userDtos.get(0).getEmail());
        assertEquals(users.get(0).getLastName(), userDtos.get(0).getLastName());
        assertEquals(users.get(0).getFirstName(), userDtos.get(0).getFirstName());
        assertEquals(users.get(0).getPassword(), userDtos.get(0).getPassword());
        assertTrue(userDtos.get(0).isAdmin());
        
        assertEquals(4L, userDtos.get(1).getId());
        assertEquals(users.get(1).getEmail(), userDtos.get(1).getEmail());
        assertEquals(users.get(1).getLastName(), userDtos.get(1).getLastName());
        assertEquals(users.get(1).getFirstName(), userDtos.get(1).getFirstName());
        assertEquals(users.get(1).getPassword(), userDtos.get(1).getPassword());
        assertFalse(userDtos.get(1).isAdmin());
    }
    // Test de mappage de UserDtos à User
    @Test
    @DisplayName("Map list of UserDtos to list of User entities")
    public void shouldMapDtoListToUserList() {
        List<UserDto> userDtos = Arrays.asList(
            userMocks.createUserDto(5L, "charlie@mail.com", "Test", "Charlie", "charliePass", true, false),
            userMocks.createUserDto(6L, "diana@mail.com", "Test", "Diana", "dianaPass", false, false)
        );

        List<User> users = userMapper.toEntity(userDtos);

        assertNotNull(users);
        assertEquals(2, users.size());
        
        assertEquals(5L, users.get(0).getId());
        assertEquals(userDtos.get(0).getEmail(), users.get(0).getEmail());
        assertEquals(userDtos.get(0).getLastName(), users.get(0).getLastName());
        assertEquals(userDtos.get(0).getFirstName(), users.get(0).getFirstName());
        assertEquals(userDtos.get(0).getPassword(), users.get(0).getPassword());
        assertTrue(users.get(0).isAdmin());
        
        assertEquals(6L, users.get(1).getId());
        assertEquals(userDtos.get(1).getEmail(), users.get(1).getEmail());
        assertEquals(userDtos.get(1).getLastName(), users.get(1).getLastName());
        assertEquals(userDtos.get(1).getFirstName(), users.get(1).getFirstName());
        assertEquals(userDtos.get(1).getPassword(), users.get(1).getPassword());
        assertFalse(users.get(1).isAdmin());
    }
    // Tests pour les valeurs longues
    @Test
    @DisplayName("Handle long values in User to UserDto mapping")
    public void shouldHandleLongValuesInUserToDto() {
        User user = userMocks.createUser(7L, "verylong.email.address@verylongdomainmail.com", "A".repeat(30), "B".repeat(30), "C".repeat(150), true, true);
        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.isAdmin(), userDto.isAdmin());
    }
    // Test de mappage de UserDto à User
    @Test
    @DisplayName("Handle long values in UserDto to User mapping")
    public void shouldHandleLongValuesInDtoToUser() {
        UserDto userDto = userMocks.createUserDto(8L, "another.very.long.email@extremelylongdomain.com", 
                                      "D".repeat(30), "E".repeat(30), "F".repeat(150), false, true);
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