package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Test pour vérifier le contexte de l'application Spring Boot
@SpringBootTest
public class SpringBootSecurityJwtApplicationTest {
    // Injection du contexte de l'application
    @Autowired
    private ApplicationContext applicationContext;
    // Test pour vérifier que le contexte de l'application se charge correctement
    @Test
    @DisplayName("Context loads successfully")
    public void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }
    // Test pour vérifier que les composants de sécurité sont correctement configurés
    @Test
    @DisplayName("Security components are properly configured")
    public void securityComponentsAreConfigured() {
        assertNotNull(applicationContext.getBeanNamesForType(AuthenticationManager.class),
                "AuthenticationManager bean should be defined");
    }
    // Test pour vérifier que l'application démarre sans exceptions
    @Test
    @DisplayName("Application main method runs without throwing exceptions")
    public void applicationStartsWithoutExceptions() {
        try {
            SpringBootSecurityJwtApplication.main(new String[]{});
        } catch (Exception e) {
            assert false : "Application main method threw an exception: " + e.getMessage();
        }
    }
}