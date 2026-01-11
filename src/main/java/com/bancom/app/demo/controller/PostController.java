package com.bancom.app.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bancom.app.demo.dto.PostCreacionRequest;
import com.bancom.app.demo.entities.Post;
import com.bancom.app.demo.entities.Usuario;
import com.bancom.app.demo.repository.UsuarioRepository;
import com.bancom.app.demo.security.annotation.AuthenticatedUserId;
import com.bancom.app.demo.service.IServicePost;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class PostController {

    @Autowired
    private IServicePost servicePost;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // API private para listar posts
    @GetMapping("/post/list")
    public List<Post> list() {
        return servicePost.findAll();
    }

    // API private para crear post
    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(
            @Valid @RequestBody PostCreacionRequest postRequest,
            @AuthenticatedUserId Long userId) {
        Post postNew = null;
        Map<String, Object> response = new HashMap<>();

        try {
            // Validar que el usuario existe
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            if (usuario == null) {
                response.put("mensaje", "Usuario no encontrado");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

            // Mapear DTO a entidad
            Post post = new Post();
            post.setText(postRequest.getText());
            post.setUserId(userId);
            post.setCreateAt(new Date());
            post.setActive(true);

            postNew = servicePost.save(post);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al registrar el post en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Error al procesar el token");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("mensaje", "El Post ha sido creado con éxito");
        response.put("post", postNew);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    // Api private para actualizar post
    @PutMapping("/post/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(
            @RequestBody Post post,
            @PathVariable Long id,
            @AuthenticatedUserId Long userId) {
        Post postActual = null;
        Map<String, Object> response = new HashMap<>();

        try {
            // Buscar el post a actualizar
            postActual = servicePost.findById(id);

            if (postActual == null) {
                response.put("mensaje", "El ID " + id + " ingresado no existe en la base de datos.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

            // Validar que el usuario del token es el dueño del post
            if (!postActual.getUserId().equals(userId)) {
                response.put("mensaje", "No tienes permisos para actualizar este post");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
            }

            postActual.setText(post.getText());
            postActual.setModifyAt(new Date());
            servicePost.save(postActual);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el post en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Error al procesar el token");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("mensaje", "El Post ha sido actualizado con éxito");
        response.put("post", postActual);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    // Api private para eliminar post
    @DeleteMapping("/post/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticatedUserId Long userId) {
        Post postDelete = null;
        Map<String, Object> response = new HashMap<>();

        try {
            postDelete = servicePost.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar el post en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (postDelete == null) {
            response.put("mensaje", "El ID " + id + " ingresado no existe en la base de datos.");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        // Validar que el usuario del token es el dueño del post
        if (!postDelete.getUserId().equals(userId)) {
            response.put("mensaje", "No tienes permisos para eliminar este post");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FORBIDDEN);
        }

        servicePost.delete(id);
        response.put("mensaje", "El Post ha sido eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}
