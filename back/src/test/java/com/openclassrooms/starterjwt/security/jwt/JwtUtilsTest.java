package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private final String testEmail = "test@example.com";
    private final String jwtSecret = "testSecret";
    private final int jwtExpirationMs = 60000;

    private UserDetailsImpl userDetails;
    private Authentication authentication;
    // Variables pour les tests
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);

        userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(testEmail);

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }
    // Test pour vérifier que le token JWT est généré correctement
    @Test
    @DisplayName("Generate token")
    public void generateJwtToken_ReturnsValidToken() {
        String token = jwtUtils.generateJwtToken(authentication);
        assertNotNull(token);
        String subject = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        assertEquals(testEmail, subject);
    }
    // Test pour vérifier que le nom d'utilisateur est extrait correctement du token JWT
    @Test
    @DisplayName("Get username from token")
    public void getUserNameFromJwtToken_ReturnsCorrectUsername() {
        String token = Jwts.builder()
                .setSubject(testEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(testEmail, username);
    }
    // Test pour vérifier la validation du token JWT
    @Test
    @DisplayName("Validate a token")
    public void validateJwtToken_WithValidToken_ReturnsTrue() {
        String token = Jwts.builder()
                .setSubject(testEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertTrue(isValid);
    }
    // Test pour vérifier la validation du token JWT expiré
    @Test
    @DisplayName("Check an invalid token")
    public void validateJwtToken_WithExpiredToken_ReturnsFalse() {
        String token = Jwts.builder()
                .setSubject(testEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertFalse(isValid);
    }
    // Test pour vérifier la validation du token JWT avec une signature invalide
    @Test
    @DisplayName("Check a token with invalid signature")
    public void validateJwtToken_WithInvalidSignature_ReturnsFalse() {
        String token = Jwts.builder()
                .setSubject(testEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertFalse(isValid);
    }
    // Test pour vérifier la validation du token JWT mal formé
    @Test
    @DisplayName("Check a malformed token")
    public void validateJwtToken_WithMalformedToken_ReturnsFalse() {
        String token = "malformed.jwt.token";

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertFalse(isValid);
    }
    // Test pour vérifier la validation du token JWT vide
    @Test
    @DisplayName("Check an emptuy token")
    public void validateJwtToken_WithEmptyToken_ReturnsFalse() {

        boolean isValid = jwtUtils.validateJwtToken("");

        assertFalse(isValid);
    }
}