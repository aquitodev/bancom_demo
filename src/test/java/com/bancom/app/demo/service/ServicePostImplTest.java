package com.bancom.app.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bancom.app.demo.entities.Post;
import com.bancom.app.demo.repository.PostRepository;

/**
 * Unit tests para ServicePostImpl
 * Usa Mockito para simular el comportamiento del repositorio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - ServicePostImpl")
class ServicePostImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ServicePostImpl servicePost;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setText("Este es un post de prueba");
        post.setUserId(1L);
        post.setCreateAt(new Date());
        post.setActive(true);
    }

    @Test
    @DisplayName("findAll() - Debe retornar lista de posts")
    void testFindAll() {
        // Given
        Post post2 = new Post();
        post2.setId(2L);
        post2.setText("Segundo post");
        List<Post> posts = Arrays.asList(post, post2);
        
        when(postRepository.findAll()).thenReturn(posts);

        // When
        List<Post> result = servicePost.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById() - Debe retornar post cuando existe")
    void testFindByIdExistente() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        Post result = servicePost.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Este es un post de prueba", result.getText());
        assertEquals(1L, result.getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() - Debe retornar null cuando no existe")
    void testFindByIdNoExistente() {
        // Given
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Post result = servicePost.findById(999L);

        // Then
        assertNull(result);
        verify(postRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("findById() - Debe retornar null cuando id es null")
    void testFindByIdNull() {
        // When
        Post result = servicePost.findById(null);

        // Then
        assertNull(result);
        verify(postRepository, never()).findById(any());
    }

    @Test
    @DisplayName("save() - Debe guardar y retornar post")
    void testSave() {
        // Given
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When
        Post result = servicePost.save(post);

        // Then
        assertNotNull(result);
        assertEquals("Este es un post de prueba", result.getText());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    @DisplayName("save() - Debe retornar null cuando post es null")
    void testSaveNull() {
        // When
        Post result = servicePost.save(null);

        // Then
        assertNull(result);
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() - Debe desactivar post (soft delete)")
    void testDelete() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When
        servicePost.delete(1L);

        // Then
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
        assertFalse(post.isActive());
    }

    @Test
    @DisplayName("delete() - No debe hacer nada si post no existe")
    void testDeleteNoExistente() {
        // Given
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        servicePost.delete(999L);

        // Then
        verify(postRepository, times(1)).findById(999L);
        verify(postRepository, never()).save(any());
    }
}
