package com.bancom.app.demo.security.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.bancom.app.demo.security.annotation.AuthenticatedUserId;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

import static com.bancom.app.demo.security.JwtTokenConfig.*;

/**
 * Resolver personalizado que extrae el userId del token JWT
 * y lo inyecta automáticamente en los parámetros anotados con @AuthenticatedUserId
 */
@Component
public class AuthenticatedUserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        // Este resolver solo soporta parámetros anotados con @AuthenticatedUserId
        return parameter.hasParameterAnnotation(AuthenticatedUserId.class) 
               && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter, 
            @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest, 
            @Nullable WebDataBinderFactory binderFactory) throws Exception {
        
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        
        // Obtener el header de autorización
        String header = request.getHeader(HEADER_AUTHORIZATION);
        
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            throw new IllegalArgumentException("Token no proporcionado o inválido");
        }
        
        // Extraer y parsear el token
        String token = header.replace(PREFIX_TOKEN, "");
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        // Retornar el userId del token
        return claims.get("id", Long.class);
    }
}
