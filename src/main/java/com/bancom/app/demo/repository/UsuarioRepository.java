package com.bancom.app.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bancom.app.demo.entities.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    @Query("SELECT u "+
           "FROM Usuario u " +
           "WHERE u.nickname = ?1 and u.password = ?2")
    public Usuario login(String nickname, String password);
    
    Optional<Usuario> findByNickname(String nickname);
}
