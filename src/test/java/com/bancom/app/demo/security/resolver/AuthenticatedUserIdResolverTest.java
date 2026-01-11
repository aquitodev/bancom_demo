package com.bancom.app.demo.security.resolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import com.bancom.app.demo.security.annotation.AuthenticatedUserId;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import static com.bancom.app.demo.security.JwtTokenConfig.HEADER_AUTHORIZATION;
import static com.bancom.app.demo.security.JwtTokenConfig.PREFIX_TOKEN;

import javax.crypto.SecretKey;

/**
 * Unit tests para AuthenticatedUserIdResolver
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - AuthenticatedUserIdResolver")
class AuthenticatedUserIdResolverTest {

    @InjectMocks
    private AuthenticatedUserIdResolver resolver;

    @Mock
    private MethodParameter methodParameter;

    private MockHttpServletRequest request;
    private NativeWebRequest webRequest;
    private String validToken;
    private static final SecretKey TEST_SECRET_KEY = Keys.hmacShaKeyFor("testSecretKeyForJWTTokenGenerationAndValidationPurposesOnly".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        webRequest = new ServletWebRequest(request);

        // Generar token válido
        validToken = Jwts.builder()
                .subject("testuser")
                .claim("id", 123L)
                .claim("nickname", "testuser")
                .claim("authorities", "[{\"authority\":\"ROLE_USER\"}]")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(TEST_SECRET_KEY)
                .compact();
    }

    @Test
    @DisplayName("supportsParameter() - Debe soportar parámetro con @AuthenticatedUserId y tipo Long")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testSupportsParameterValido() {
        when(methodParameter.hasParameterAnnotation(AuthenticatedUserId.class)).thenReturn(true);
        when(methodParameter.getParameterType()).thenReturn((Class) Long.class);

        boolean result = resolver.supportsParameter(methodParameter);

        assertTrue(result);
    }

    @Test
    @DisplayName("supportsParameter() - No debe soportar parámetro sin anotación")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testSupportsParameterSinAnotacion() {
        when(methodParameter.hasParameterAnnotation(AuthenticatedUserId.class)).thenReturn(false);
        when(methodParameter.getParameterType()).thenReturn((Class) Long.class);

        boolean result = resolver.supportsParameter(methodParameter);

        assertFalse(result);
    }

    @Test
    @DisplayName("supportsParameter() - No debe soportar parámetro con tipo incorrecto")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testSupportsParameterTipoIncorrecto() {
        when(methodParameter.hasParameterAnnotation(AuthenticatedUserId.class)).thenReturn(true);
        when(methodParameter.getParameterType()).thenReturn((Class) String.class);

        boolean result = resolver.supportsParameter(methodParameter);

        assertFalse(result);
    }

    @Test
    @DisplayName("resolveArgument() - Debe extraer userId del token válido")
    void testResolveArgumentTokenValido() throws Exception {
        request.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + validToken);

        Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertNotNull(result);
        assertEquals(123L, result);
    }

    @Test
    @DisplayName("resolveArgument() - Debe lanzar excepción si no hay header")
    void testResolveArgumentSinHeader() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolveArgument(methodParameter, null, webRequest, null);
        });

        assertEquals("Token no proporcionado o inválido", exception.getMessage());
    }

    @Test
    @DisplayName("resolveArgument() - Debe lanzar excepción si header no tiene Bearer")
    void testResolveArgumentSinBearer() {
        request.addHeader(HEADER_AUTHORIZATION, "InvalidPrefix " + validToken);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolveArgument(methodParameter, null, webRequest, null);
        });

        assertEquals("Token no proporcionado o inválido", exception.getMessage());
    }

    @Test
    @DisplayName("resolveArgument() - Debe lanzar excepción si token es inválido")
    void testResolveArgumentTokenInvalido() {
        request.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + "tokeninvalido");

        assertThrows(Exception.class, () -> {
            resolver.resolveArgument(methodParameter, null, webRequest, null);
        });
    }

    @Test
    @DisplayName("resolveArgument() - Debe lanzar excepción si token expiró")
    void testResolveArgumentTokenExpirado() {
        // Generar token expirado (expiró hace 1 hora)
        String expiredToken = Jwts.builder()
                .subject("testuser")
                .claim("id", 123L)
                .issuedAt(new Date(System.currentTimeMillis() - 7200000))
                .expiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(TEST_SECRET_KEY)
                .compact();

        request.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + expiredToken);

        assertThrows(Exception.class, () -> {
            resolver.resolveArgument(methodParameter, null, webRequest, null);
        });
    }
}
