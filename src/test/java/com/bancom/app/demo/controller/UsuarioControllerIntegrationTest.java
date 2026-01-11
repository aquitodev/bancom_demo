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
 * Integration tests para UsuarioController
 * Usa MockMvc para simular peticiones HTTP
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - UsuarioController")
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Crear usuario exitosamente")
    void testCrearUsuarioExitoso() throws Exception {
        String usuarioJson = """
            {
                "cellphone": "0987654321",
                "name": "Juan",
                "lastname": "Pérez",
                "nickname": "juanp",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El Usuario ha sido creado con éxito"))
                .andExpect(jsonPath("$.usuario.nickname").value("juanp"))
                .andExpect(jsonPath("$.usuario.name").value("Juan"))
                .andExpect(jsonPath("$.usuario.active").value(true))
                .andExpect(jsonPath("$.usuario.id").exists());
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Validación: cellphone inválido")
    void testCrearUsuarioCellphoneInvalido() throws Exception {
        String usuarioJson = """
            {
                "cellphone": "123",
                "name": "Juan",
                "lastname": "Pérez",
                "nickname": "juanp",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.cellphone").exists());
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Validación: name vacío")
    void testCrearUsuarioNameVacio() throws Exception {
        String usuarioJson = """
            {
                "cellphone": "0987654321",
                "name": "",
                "lastname": "Pérez",
                "nickname": "juanp",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Validación: nickname muy corto")
    void testCrearUsuarioNicknameMuyCorto() throws Exception {
        String usuarioJson = """
            {
                "cellphone": "0987654321",
                "name": "Juan",
                "lastname": "Pérez",
                "nickname": "ab",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nickname").exists());
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Validación: nickname con caracteres inválidos")
    void testCrearUsuarioNicknameCaracteresInvalidos() throws Exception {
        String usuarioJson = """
            {
                "cellphone": "0987654321",
                "name": "Juan",
                "lastname": "Pérez",
                "nickname": "juan@perez",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nickname").exists());
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Validación: password muy corto")
    void testCrearUsuarioPasswordMuyCorto() throws Exception {
        String usuarioJson = """
            {
                "cellphone": "0987654321",
                "name": "Juan",
                "lastname": "Pérez",
                "nickname": "juanp",
                "password": "12345"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("GET /api/v1/usuario/list - Listar todos los usuarios")
    void testListarUsuarios() throws Exception {
        // Crear usuarios de prueba
        Usuario usuario1 = new Usuario();
        usuario1.setCellphone("0987654321");
        usuario1.setName("Juan");
        usuario1.setLastname("Pérez");
        usuario1.setNickname("juanp");
        usuario1.setPassword("password123");
        usuario1.setCreateAt(new Date());
        usuario1.setActive(true);
        usuarioRepository.save(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setCellphone("0987654322");
        usuario2.setName("María");
        usuario2.setLastname("García");
        usuario2.setNickname("mariag");
        usuario2.setPassword("password456");
        usuario2.setCreateAt(new Date());
        usuario2.setActive(true);
        usuarioRepository.save(usuario2);

        mockMvc.perform(get("/api/v1/usuario/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nickname").value("juanp"))
                .andExpect(jsonPath("$[1].nickname").value("mariag"));
    }

    @Test
    @DisplayName("GET /api/v1/usuario/list - Lista vacía cuando no hay usuarios")
    void testListarUsuariosVacio() throws Exception {
        mockMvc.perform(get("/api/v1/usuario/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/v1/usuario - Nickname duplicado debe retornar error")
    void testCrearUsuarioNicknameDuplicado() throws Exception {
        // Crear primer usuario
        Usuario usuario1 = new Usuario();
        usuario1.setCellphone("0987654321");
        usuario1.setName("Juan");
        usuario1.setLastname("Pérez");
        usuario1.setNickname("juanp");
        usuario1.setPassword("password123");
        usuario1.setCreateAt(new Date());
        usuario1.setActive(true);
        usuarioRepository.save(usuario1);

        // Intentar crear otro usuario con el mismo nickname
        String usuarioJson = """
            {
                "cellphone": "0987654322",
                "name": "Pedro",
                "lastname": "López",
                "nickname": "juanp",
                "password": "password456"
            }
            """;

        mockMvc.perform(post("/api/v1/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al registrar el usuario en la base de datos."));
    }
}
