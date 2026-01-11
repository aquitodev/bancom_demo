package com.bancom.app.demo.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación personalizada para inyectar automáticamente el userId 
 * del usuario autenticado desde el token JWT en los parámetros del método.
 * 
 * Uso:
 * <pre>
 * public ResponseEntity<?> create(@AuthenticatedUserId Long userId, ...) {
 *     // userId ya contiene el ID del usuario del token
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticatedUserId {
}
