package com.bancom.app.demo.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bancom.app.demo.entities.Post;

/**
 * Integration tests para PostRepository
 * Usa @DataJpaTest que configura una base de datos en memoria
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de Repositorio - PostRepository")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();

        post = new Post();
        post.setText("Este es un post de prueba");
        post.setUserId(1L);
        post.setCreateAt(new Date());
        post.setActive(true);
    }

    @Test
    @DisplayName("save() - Debe guardar post correctamente")
    void testSave() {
        Post savedPost = postRepository.save(post);

        assertNotNull(savedPost.getId());
        assertEquals("Este es un post de prueba", savedPost.getText());
        assertEquals(1L, savedPost.getUserId());
        assertTrue(savedPost.isActive());
    }

    @Test
    @DisplayName("findById() - Debe encontrar post por ID")
    void testFindById() {
        Post savedPost = postRepository.save(post);

        Optional<Post> found = postRepository.findById(savedPost.getId());

        assertTrue(found.isPresent());
        assertEquals("Este es un post de prueba", found.get().getText());
    }

    @Test
    @DisplayName("findById() - Debe retornar empty cuando no existe")
    void testFindByIdNoExiste() {
        Optional<Post> found = postRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("delete() - Debe eliminar post correctamente")
    void testDelete() {
        Post savedPost = postRepository.save(post);
        Long id = savedPost.getId();

        postRepository.deleteById(id);

        Optional<Post> found = postRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("findAll() - Debe retornar todos los posts")
    void testFindAll() {
        postRepository.save(post);

        Post post2 = new Post();
        post2.setText("Segundo post de prueba");
        post2.setUserId(2L);
        post2.setCreateAt(new Date());
        post2.setActive(true);
        postRepository.save(post2);

        List<Post> posts = (List<Post>) postRepository.findAll();

        assertEquals(2, posts.size());
    }

    @Test
    @DisplayName("update() - Debe actualizar post correctamente")
    void testUpdate() {
        Post savedPost = postRepository.save(post);
        savedPost.setText("Post actualizado");
        savedPost.setModifyAt(new Date());

        Post updatedPost = postRepository.save(savedPost);

        assertEquals("Post actualizado", updatedPost.getText());
        assertNotNull(updatedPost.getModifyAt());
    }

    @Test
    @DisplayName("Debe permitir múltiples posts del mismo usuario")
    void testMultiplesPostsMismoUsuario() {
        postRepository.save(post);

        Post post2 = new Post();
        post2.setText("Segundo post del mismo usuario");
        post2.setUserId(1L); // Mismo userId
        post2.setCreateAt(new Date());
        post2.setActive(true);
        postRepository.save(post2);

        List<Post> posts = (List<Post>) postRepository.findAll();

        assertEquals(2, posts.size());
        assertEquals(1L, posts.get(0).getUserId());
        assertEquals(1L, posts.get(1).getUserId());
    }

    @Test
    @DisplayName("Soft delete - Debe marcar post como inactivo")
    void testSoftDelete() {
        Post savedPost = postRepository.save(post);
        savedPost.setActive(false);

        Post updatedPost = postRepository.save(savedPost);

        assertFalse(updatedPost.isActive());
        // El post aún existe en la base de datos
        Optional<Post> found = postRepository.findById(updatedPost.getId());
        assertTrue(found.isPresent());
    }
}
