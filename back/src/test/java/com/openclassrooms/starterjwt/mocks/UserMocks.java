package com.openclassrooms.starterjwt.mocks;

import java.time.LocalDateTime;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

// Utility class to create mock User, UserDto, SignupRequest, and LoginRequest objects for testing
public class UserMocks {
    private final LocalDateTime createdAt = LocalDateTime.parse("2025-03-04T12:03:35");
    private final LocalDateTime updatedAt = LocalDateTime.parse("2025-03-04T12:03:35");
    // Method to create a mock User object
    public User createUser(Long id, String email, String lastName, String firstName,
            String password, Boolean admin, Boolean longValues) {
        String longEmail = "extremely.long.email.address.that.exceeds.fifty.characters@verylongdomainname.com";
        String longLastName = "A".repeat(30);
        String longFirstName = "B".repeat(30);
        String longPassword = "C".repeat(150);
        // Build and return the User object with provided or default values
        return User.builder()
                .id(id)
                .email(longValues ? longEmail : email)
                .lastName(longValues ? longLastName : lastName)
                .firstName(longValues ? longFirstName : firstName)
                .password(longValues ? longPassword : password)
                .admin(admin)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
    // Method to create a mock UserDto object
    public UserDto createUserDto(Long id, String email, String lastName, String firstName,
            String password, Boolean admin, Boolean longValues) {
        String longEmail = "different.extremely.long.email@verylongdomainname.com";
        String longLastName = "D".repeat(30);
        String longFirstName = "E".repeat(30);
        String longPassword = "F".repeat(150);

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setEmail(longValues ? longEmail : email);
        userDto.setLastName(longValues ? longLastName : lastName);
        userDto.setFirstName(longValues ? longFirstName : firstName);
        userDto.setPassword(longValues ? longPassword : password);
        userDto.setAdmin(admin);
        userDto.setCreatedAt(createdAt);
        userDto.setUpdatedAt(updatedAt);
        return userDto;
    }
    // Method to create a mock SignupRequest object
    public SignupRequest createSignupRequest(String email, String lastName, String firstName, String password) {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(email);
        signupRequest.setLastName(lastName);
        signupRequest.setFirstName(firstName);
        signupRequest.setPassword(password);
        return signupRequest;
    }
    // Method to create a mock LoginRequest object
    public LoginRequest createLoginRequest(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        return loginRequest;
    }

}