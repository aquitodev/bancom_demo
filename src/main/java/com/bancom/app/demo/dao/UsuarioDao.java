package com.bancom.app.demo.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bancom.app.demo.model.Usuario;

@Repository
public interface UsuarioDao extends CrudRepository<Usuario, Long> {

    @Query("SELECT u "+
           "FROM Usuario u " +
           "WHERE u.nickname = ?1 and u.password = ?2")
    public Usuario login(String nickname, String password);
}
