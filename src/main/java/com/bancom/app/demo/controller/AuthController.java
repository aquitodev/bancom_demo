package com.bancom.app.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancom.app.demo.dto.LoginRequest;
import com.bancom.app.demo.entities.Usuario;
import com.bancom.app.demo.service.IServiceUsuario;

import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

import static com.bancom.app.demo.security.JwtTokenConfig.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class AuthController {
    @Autowired
    private IServiceUsuario serviceUsuario;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Buscar usuario por nickname
        Usuario usuarioLogueado = serviceUsuario.findByNickname(loginRequest.getNickname());
        
        // Validar que exista el usuario y la contraseña sea correcta (texto plano)
        if (usuarioLogueado == null || !usuarioLogueado.getPassword().equals(loginRequest.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error en la autenticacion username o password incorrectos!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        // Generar token JWT
        String token = Jwts.builder()
                .subject(usuarioLogueado.getNickname())
                .claim("nickname", usuarioLogueado.getNickname())
                .claim("id", usuarioLogueado.getId())
                .claim("authorities", "[{\"authority\":\"ROLE_USER\"}]")
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .issuedAt(new Date())
                .signWith(SECRET_KEY)
                .compact();
        
        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("nickname", usuarioLogueado.getNickname());
        response.put("message", String.format("Hola %s has iniciado sesion con exito!", usuarioLogueado.getNickname()));
        
        return ResponseEntity.ok(response);
    }
}
