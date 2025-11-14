package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class AuthTokenFilterTest {

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private AuthTokenFilter authTokenFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private UserDetails userDetails;

    private static final String VALID_TOKEN = "validToken";
    private static final String USERNAME = "testuser";
    // Variables pour les tests
    @BeforeEach
    void setUp() {
        authTokenFilter = new AuthTokenFilter();

        ReflectionTestUtils.setField(authTokenFilter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authTokenFilter, "userDetailsService", userDetailsService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        SecurityContextHolder.clearContext();

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(USERNAME);
    }
    // Test unitaire pour si le token JWT est valide et l'utilisateur est authentifié
    @Test
    @DisplayName("Should authenticate user when valid JWT token is provided")
    void doFilterInternal_ShouldAuthenticateUser_WhenValidJwtTokenIsProvided() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + VALID_TOKEN);
        
        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(VALID_TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils).validateJwtToken(VALID_TOKEN);
        verify(jwtUtils).getUserNameFromJwtToken(VALID_TOKEN);
        verify(userDetailsService).loadUserByUsername(USERNAME);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
    }
    // Test unitaire pour si le token JWT est invalide
    @Test
    @DisplayName("Should not authenticate user when invalid JWT token is provided")
    void doFilterInternal_ShouldNotAuthenticateUser_WhenInvalidJwtTokenIsProvided() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalidToken");
        
        when(jwtUtils.validateJwtToken("invalidToken")).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils).validateJwtToken("invalidToken");
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    // Test unitaire pour si aucun token JWT n'est fourni
    @Test
    @DisplayName("Should not authenticate user when no JWT token is provided")
    void doFilterInternal_ShouldNotAuthenticateUser_WhenNoJwtTokenIsProvided() throws ServletException, IOException {
        authTokenFilter.doFilterInternal(request, response, filterChain);
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    // Test uniataire pour gérer les exceptions
    @Test
    @DisplayName("Should handle exceptions")
    void doFilterInternal_ShouldHandleExceptions() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + VALID_TOKEN);
        
        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(VALID_TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenThrow(new UsernameNotFoundException("User not found"));

        authTokenFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    // Test unitaire pour si le token JWT est correctement extrait de l'en-tête Authorization
    @Test
    @DisplayName("Should correctly parse JWT token from Authorization header")
    void parseJwt_ShouldExtractToken_WhenAuthorizationHeaderIsValid() throws Exception {
        String token = "testToken";
        request.addHeader("Authorization", "Bearer " + token);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils).validateJwtToken(token);
    }
    // Test unitaire pour si le token JWT est invalide
    @Test
    @DisplayName("Should return null when Authorization header is missing")
    void parseJwt_ShouldReturnNull_WhenAuthorizationHeaderIsMissing() throws Exception {
        authTokenFilter.doFilterInternal(request, response, filterChain);
        verify(jwtUtils, never()).validateJwtToken(anyString());
    }
    // Test unitaire pour si le token JWT est invalide
    @Test
    @DisplayName("Should return null when Authorization header doesn't start with Bearer")
    void parseJwt_ShouldReturnNull_WhenAuthorizationHeaderDoesntStartWithBearer() throws Exception {
        request.addHeader("Authorization", "Basic dGVzdDp0ZXN0");
        authTokenFilter.doFilterInternal(request, response, filterChain);
        verify(jwtUtils, never()).validateJwtToken(anyString());
    }
}