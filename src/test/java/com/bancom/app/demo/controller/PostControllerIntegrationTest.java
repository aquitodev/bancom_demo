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

import com.bancom.app.demo.entities.Post;
import com.bancom.app.demo.entities.Usuario;
import com.bancom.app.demo.repository.PostRepository;
import com.bancom.app.demo.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import static com.bancom.app.demo.security.JwtTokenConfig.HEADER_AUTHORIZATION;
import static com.bancom.app.demo.security.JwtTokenConfig.PREFIX_TOKEN;

import javax.crypto.SecretKey;

/**
 * Integration tests para PostController
 * Usa MockMvc para simular peticiones HTTP con autenticación JWT
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - PostController")
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private String tokenUsuario1;
    private String tokenUsuario2;
    private Post post1;
    private static final SecretKey TEST_SECRET_KEY = Keys.hmacShaKeyFor("testSecretKeyForJWTTokenGenerationAndValidationPurposesOnly".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuarios de prueba
        usuario1 = new Usuario();
        usuario1.setCellphone("0987654321");
        usuario1.setName("Juan");
        usuario1.setLastname("Pérez");
        usuario1.setNickname("juanp");
        usuario1.setPassword("password123");
        usuario1.setCreateAt(new Date());
        usuario1.setActive(true);
        usuario1 = usuarioRepository.save(usuario1);

        usuario2 = new Usuario();
        usuario2.setCellphone("0987654322");
        usuario2.setName("María");
        usuario2.setLastname("García");
        usuario2.setNickname("mariag");
        usuario2.setPassword("password456");
        usuario2.setCreateAt(new Date());
        usuario2.setActive(true);
        usuario2 = usuarioRepository.save(usuario2);

        // Generar tokens JWT
        tokenUsuario1 = generateToken(usuario1);
        tokenUsuario2 = generateToken(usuario2);

        // Crear post de prueba
        post1 = new Post();
        post1.setText("Post de prueba de Juan");
        post1.setUserId(usuario1.getId());
        post1.setCreateAt(new Date());
        post1.setActive(true);
        post1 = postRepository.save(post1);
    }

    private String generateToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getNickname())
                .claim("id", usuario.getId())
                .claim("nickname", usuario.getNickname())
                .claim("authorities", "[{\"authority\":\"ROLE_USER\"}]")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(TEST_SECRET_KEY)
                .compact();
    }

    @Test
    @DisplayName("GET /api/v1/post/list - Listar posts con autenticación")
    void testListarPosts() throws Exception {
        mockMvc.perform(get("/api/v1/post/list")
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].text").exists());
    }

    @Test
    @DisplayName("GET /api/v1/post/list - Sin token debe retornar 401")
    void testListarPostsSinToken() throws Exception {
        mockMvc.perform(get("/api/v1/post/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // Spring Security devuelve 403 por defecto
    }

    @Test
    @DisplayName("POST /api/v1/post - Crear post exitosamente")
    void testCrearPostExitoso() throws Exception {
        String postJson = """
            {
                "text": "Este es un nuevo post de prueba"
            }
            """;

        mockMvc.perform(post("/api/v1/post")
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El Post ha sido creado con éxito"))
                .andExpect(jsonPath("$.post.text").value("Este es un nuevo post de prueba"))
                .andExpect(jsonPath("$.post.userId").value(usuario1.getId()))
                .andExpect(jsonPath("$.post.active").value(true))
                .andExpect(jsonPath("$.post.id").exists());
    }

    @Test
    @DisplayName("POST /api/v1/post - Sin token debe retornar 403")
    void testCrearPostSinToken() throws Exception {
        String postJson = """
            {
                "text": "Este es un nuevo post"
            }
            """;

        mockMvc.perform(post("/api/v1/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/post - Validación: text vacío debe retornar 400")
    void testCrearPostTextVacio() throws Exception {
        String postJson = """
            {
                "text": ""
            }
            """;

        mockMvc.perform(post("/api/v1/post")
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.text").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/post/{id} - Actualizar post propio exitosamente")
    void testActualizarPostPropioExitoso() throws Exception {
        String postJson = """
            {
                "text": "Post actualizado"
            }
            """;

        mockMvc.perform(put("/api/v1/post/" + post1.getId())
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El Post ha sido actualizado con éxito"))
                .andExpect(jsonPath("$.post.text").value("Post actualizado"))
                .andExpect(jsonPath("$.post.id").value(post1.getId()));
    }

    @Test
    @DisplayName("PUT /api/v1/post/{id} - No debe permitir actualizar post de otro usuario")
    void testActualizarPostAjenoForbidden() throws Exception {
        String postJson = """
            {
                "text": "Intento de actualizar post ajeno"
            }
            """;

        mockMvc.perform(put("/api/v1/post/" + post1.getId())
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("No tienes permisos para actualizar este post"));
    }

    @Test
    @DisplayName("PUT /api/v1/post/{id} - Actualizar post inexistente debe retornar 404")
    void testActualizarPostInexistente() throws Exception {
        String postJson = """
            {
                "text": "Post actualizado"
            }
            """;

        mockMvc.perform(put("/api/v1/post/999")
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El ID 999 ingresado no existe en la base de datos."));
    }

    @Test
    @DisplayName("DELETE /api/v1/post/{id} - Eliminar post exitosamente")
    void testEliminarPostExitoso() throws Exception {
        mockMvc.perform(delete("/api/v1/post/" + post1.getId())
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El Post ha sido eliminado con éxito"));
    }

    @Test
    @DisplayName("DELETE /api/v1/post/{id} - Eliminar post inexistente debe retornar 404")
    void testEliminarPostInexistente() throws Exception {
        mockMvc.perform(delete("/api/v1/post/999")
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + tokenUsuario1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El ID 999 ingresado no existe en la base de datos."));
    }

    @Test
    @DisplayName("DELETE /api/v1/post/{id} - Sin token debe retornar 403")
    void testEliminarPostSinToken() throws Exception {
        mockMvc.perform(delete("/api/v1/post/" + post1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/post - Token inválido debe retornar 401")
    void testCrearPostTokenInvalido() throws Exception {
        String postJson = """
            {
                "text": "Nuevo post"
            }
            """;

        mockMvc.perform(post("/api/v1/post")
                .header(HEADER_AUTHORIZATION, PREFIX_TOKEN + "tokeninvalido")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isUnauthorized());
    }
}
