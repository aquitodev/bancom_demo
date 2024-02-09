package com.bancom.app.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.bancom.app.demo.service.IServicePost;

import com.bancom.app.demo.model.Post;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class PostController {
    
    @Autowired
    private IServicePost servicePost;

    @GetMapping("/post/list")
    public List<Post> list() {
        return servicePost.findAll();
    }

    @PreAuthorize("")
    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody Post post) {
        Post postNew = null;
        Map<String,Object> response = new HashMap<>();

        try {
            post.setCreateAt(new Date());
            postNew = servicePost.save(post);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El Post ha sido creado con éxito");
        response.put("post", postNew);
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/post/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody Post post, @PathVariable Long id) {
        Post postActual = null;
        Map<String,Object> response = new HashMap<>();

        try {
            postActual = servicePost.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (postActual == null) {
            response.put("mensaje", "El ID " + id + " ingresado no existe en la base de datos.");
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
        }

        postActual.setText(post.getText());
        postActual.setModifyAt(new Date());
        servicePost.save(postActual);

        response.put("mensaje", "El Post ha sido actualizado con éxito");
        response.put("post", postActual);
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/post/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Post postDelete = null;
        Map<String,Object> response = new HashMap<>();

        try {
            postDelete = servicePost.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (postDelete == null) {
            response.put("mensaje", "El ID " + id + " ingresado no existe en la base de datos.");
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
        }

        servicePost.delete(id);

        response.put("mensaje", "El Post ha sido eliminado con éxito");
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
    }
}
