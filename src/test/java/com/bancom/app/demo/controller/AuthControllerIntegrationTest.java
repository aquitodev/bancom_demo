package com.bancom.app.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bancom.app.demo.entities.Usuario;
import com.bancom.app.demo.repository.UsuarioRepository;

/**
 * Integration tests para AuthController
 * Usa MockMvc para simular peticiones HTTP
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - AuthController")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        
        usuario = new Usuario();
        usuario.setCellphone("0987654321");
        usuario.setName("Juan");
        usuario.setLastname("Pérez");
        usuario.setNickname("juanp");
        usuario.setPassword("password123");
        usuario.setCreateAt(new Date());
        usuario.setActive(true);
        
        usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Login exitoso debe retornar token JWT")
    void testLoginExitoso() throws Exception {
        String loginJson = """
            {
                "nickname": "juanp",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token", startsWith("eyJ"))) // JWT siempre empieza con eyJ
                .andExpect(jsonPath("$.nickname").value("juanp"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Credenciales incorrectas debe retornar 401")
    void testLoginCredencialesIncorrectas() throws Exception {
        String loginJson = """
            {
                "nickname": "juanp",
                "password": "wrongpassword"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Usuario no existente debe retornar 401")
    void testLoginUsuarioNoExiste() throws Exception {
        String loginJson = """
            {
                "nickname": "noexiste",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Validación: nickname vacío debe retornar 400")
    void testLoginNicknameVacio() throws Exception {
        String loginJson = """
            {
                "nickname": "",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nickname").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Validación: nickname muy corto debe retornar 400")
    void testLoginNicknameMuyCorto() throws Exception {
        String loginJson = """
            {
                "nickname": "ab",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nickname").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Validación: password vacío debe retornar 400")
    void testLoginPasswordVacio() throws Exception {
        String loginJson = """
            {
                "nickname": "juanp",
                "password": ""
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Usuario inactivo debe retornar 401")
    void testLoginUsuarioInactivo() throws Exception {
        usuario.setActive(false);
        usuarioRepository.save(usuario);

        String loginJson = """
            {
                "nickname": "juanp",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario inactivo"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - JSON malformado debe retornar 400")
    void testLoginJsonMalformado() throws Exception {
        String loginJson = "{ invalid json }";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isBadRequest());
    }
}
