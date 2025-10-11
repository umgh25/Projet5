package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

// Test unitaire pour AuthEntryPointJwt
@SpringBootTest
public class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;
    private ObjectMapper objectMapper;
    
    @Autowired
    public AuthEntryPointJwtTest(AuthEntryPointJwt authEntryPointJwt, ObjectMapper objectMapper) {
        this.authEntryPointJwt = authEntryPointJwt;
        this.objectMapper = objectMapper;
    }
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