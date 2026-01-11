package com.bancom.app.demo.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bancom.app.demo.entities.Usuario;

/**
 * Integration tests para UsuarioRepository
 * Usa @DataJpaTest que configura una base de datos en memoria
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de Repositorio - UsuarioRepository")
class UsuarioRepositoryTest {

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
    }

    @Test
    @DisplayName("save() - Debe guardar usuario correctamente")
    void testSave() {
        Usuario savedUsuario = usuarioRepository.save(usuario);

        assertNotNull(savedUsuario.getId());
        assertEquals("juanp", savedUsuario.getNickname());
        assertEquals("Juan", savedUsuario.getName());
    }

    @Test
    @DisplayName("findById() - Debe encontrar usuario por ID")
    void testFindById() {
        Usuario savedUsuario = usuarioRepository.save(usuario);

        Optional<Usuario> found = usuarioRepository.findById(savedUsuario.getId());

        assertTrue(found.isPresent());
        assertEquals("juanp", found.get().getNickname());
    }

    @Test
    @DisplayName("findByNickname() - Debe encontrar usuario por nickname")
    void testFindByNickname() {
        usuarioRepository.save(usuario);

        Optional<Usuario> found = usuarioRepository.findByNickname("juanp");

        assertTrue(found.isPresent());
        assertEquals("juanp", found.get().getNickname());
        assertEquals("Juan", found.get().getName());
    }

    @Test
    @DisplayName("findByNickname() - Debe retornar empty cuando no existe")
    void testFindByNicknameNoExiste() {
        Optional<Usuario> found = usuarioRepository.findByNickname("noexiste");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("delete() - Debe eliminar usuario correctamente")
    void testDelete() {
        Usuario savedUsuario = usuarioRepository.save(usuario);
        Long id = savedUsuario.getId();

        usuarioRepository.deleteById(id);

        Optional<Usuario> found = usuarioRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("findAll() - Debe retornar todos los usuarios")
    void testFindAll() {
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario();
        usuario2.setCellphone("0987654322");
        usuario2.setName("María");
        usuario2.setLastname("García");
        usuario2.setNickname("mariag");
        usuario2.setPassword("password456");
        usuario2.setCreateAt(new Date());
        usuario2.setActive(true);
        usuarioRepository.save(usuario2);

        Iterable<Usuario> usuarios = usuarioRepository.findAll();

        assertEquals(2, ((java.util.Collection<?>) usuarios).size());
    }

    @Test
    @DisplayName("update() - Debe actualizar usuario correctamente")
    void testUpdate() {
        Usuario savedUsuario = usuarioRepository.save(usuario);
        savedUsuario.setName("Juan Carlos");
        savedUsuario.setModifyAt(new Date());

        Usuario updatedUsuario = usuarioRepository.save(savedUsuario);

        assertEquals("Juan Carlos", updatedUsuario.getName());
        assertNotNull(updatedUsuario.getModifyAt());
    }

    @Test
    @DisplayName("Constraints - nickname debe ser único")
    void testNicknameUnico() {
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario();
        usuario2.setCellphone("0987654322");
        usuario2.setName("Pedro");
        usuario2.setLastname("López");
        usuario2.setNickname("juanp"); // Mismo nickname
        usuario2.setPassword("password456");
        usuario2.setCreateAt(new Date());
        usuario2.setActive(true);

        assertThrows(Exception.class, () -> {
            usuarioRepository.save(usuario2); // Fuerza la validación
        });
    }

    @Test
    @DisplayName("Constraints - cellphone debe ser único")
    void testCellphoneUnico() {
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario();
        usuario2.setCellphone("0987654321"); // Mismo cellphone
        usuario2.setName("Pedro");
        usuario2.setLastname("López");
        usuario2.setNickname("pedrol");
        usuario2.setPassword("password456");
        usuario2.setCreateAt(new Date());
        usuario2.setActive(true);

        assertThrows(Exception.class, () -> {
            usuarioRepository.save(usuario2);
        });
    }
}
