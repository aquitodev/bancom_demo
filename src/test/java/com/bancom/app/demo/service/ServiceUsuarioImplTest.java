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

import com.bancom.app.demo.entities.Usuario;
import com.bancom.app.demo.repository.UsuarioRepository;

/**
 * Unit tests para ServiceUsuarioImpl
 * Usa Mockito para simular el comportamiento del repositorio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - ServiceUsuarioImpl")
class ServiceUsuarioImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ServiceUsuarioImpl serviceUsuario;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCellphone("0987654321");
        usuario.setName("Juan");
        usuario.setLastname("Pérez");
        usuario.setNickname("juanp");
        usuario.setPassword("password123");
        usuario.setCreateAt(new Date());
        usuario.setActive(true);
    }

    @Test
    @DisplayName("findAll() - Debe retornar lista de usuarios")
    void testFindAll() {
        // Given
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNickname("maria");
        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> result = serviceUsuario.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById() - Debe retornar usuario cuando existe")
    void testFindByIdExistente() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        Usuario result = serviceUsuario.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("juanp", result.getNickname());
        assertEquals(1L, result.getId());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() - Debe retornar null cuando no existe")
    void testFindByIdNoExistente() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Usuario result = serviceUsuario.findById(999L);

        // Then
        assertNull(result);
        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("findById() - Debe retornar null cuando id es null")
    void testFindByIdNull() {
        // When
        Usuario result = serviceUsuario.findById(null);

        // Then
        assertNull(result);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    @DisplayName("save() - Debe guardar y retornar usuario")
    void testSave() {
        // Given
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario result = serviceUsuario.save(usuario);

        // Then
        assertNotNull(result);
        assertEquals("juanp", result.getNickname());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("save() - Debe retornar null cuando usuario es null")
    void testSaveNull() {
        // When
        Usuario result = serviceUsuario.save(null);

        // Then
        assertNull(result);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() - Debe desactivar usuario (soft delete)")
    void testDelete() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        serviceUsuario.delete(1L);

        // Then
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
        assertFalse(usuario.isActive());
    }

    @Test
    @DisplayName("delete() - No debe hacer nada si usuario no existe")
    void testDeleteNoExistente() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        serviceUsuario.delete(999L);

        // Then
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("findByNickname() - Debe retornar usuario cuando existe")
    void testFindByNickname() {
        // Given
        when(usuarioRepository.findByNickname("juanp")).thenReturn(Optional.of(usuario));

        // When
        Usuario result = serviceUsuario.findByNickname("juanp");

        // Then
        assertNotNull(result);
        assertEquals("juanp", result.getNickname());
        verify(usuarioRepository, times(1)).findByNickname("juanp");
    }

    @Test
    @DisplayName("findByNickname() - Debe retornar null cuando no existe")
    void testFindByNicknameNoExistente() {
        // Given
        when(usuarioRepository.findByNickname("noexiste")).thenReturn(Optional.empty());

        // When
        Usuario result = serviceUsuario.findByNickname("noexiste");

        // Then
        assertNull(result);
        verify(usuarioRepository, times(1)).findByNickname("noexiste");
    }
}
