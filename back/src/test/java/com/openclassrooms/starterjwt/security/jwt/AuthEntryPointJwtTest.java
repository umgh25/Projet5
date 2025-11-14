package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test unitaire pour AuthEntryPointJwt
@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;
    private ObjectMapper objectMapper = new ObjectMapper();

    
    // Test pour vérifier que la méthode commence() configure correctement la réponse HTTP
    @Test
    @DisplayName("Entry point should set unauthorized response with correct JSON")
    public void commence_SetsUnauthorizedResponseWithCorrectJson() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = mock(AuthenticationException.class);
        when(authException.getMessage()).thenReturn("Test auth exception");

        authEntryPointJwt.commence(request, response, authException);
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        

        Map<String, Object> responseBody = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, responseBody.get("status"));
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Test auth exception", responseBody.get("message"));
        assertEquals("/api/test", responseBody.get("path"));
    }
}