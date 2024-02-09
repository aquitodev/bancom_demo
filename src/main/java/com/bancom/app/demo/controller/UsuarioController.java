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

import com.bancom.app.demo.service.IServiceUsuario;
import com.bancom.app.demo.model.Usuario;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class UsuarioController {
    
    @Autowired
    private IServiceUsuario serviceUsuario;

    @GetMapping("/usuario/list")
    public List<Usuario> list() {
        return serviceUsuario.findAll();
    }

    @PostMapping("/usuario")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        Usuario usuarioNew = null;
        Map<String,Object> response = new HashMap<>();

        try {
            usuario.setCreateAt(new Date());
            usuarioNew = serviceUsuario.save(usuario);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El Usuario ha sido creado con éxito");
        response.put("usuario", usuarioNew);
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/usuario/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody Usuario usuario, @PathVariable Long id) {
        Usuario userActual = null;
        Map<String,Object> response = new HashMap<>();

        try {
            userActual = serviceUsuario.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userActual == null) {
            response.put("mensaje", "El ID " + id + " ingresado no existe en la base de datos.");
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
        }

        userActual.setCellphone(usuario.getCellphone());
        userActual.setName(usuario.getName());
        userActual.setLastname(usuario.getLastname());
        userActual.setPassword(usuario.getPassword());
        serviceUsuario.save(userActual);

        response.put("mensaje", "El Usuario ha sido actualizado con éxito");
        response.put("usuario", userActual);
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/usuario/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Usuario userDelete = null;
        Map<String,Object> response = new HashMap<>();

        try {
            userDelete = serviceUsuario.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userDelete == null) {
            response.put("mensaje", "El ID " + id + " ingresado no existe en la base de datos.");
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
        }

        serviceUsuario.delete(id);

        response.put("mensaje", "El Usuario ha sido eliminado con éxito");
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
    }
}
