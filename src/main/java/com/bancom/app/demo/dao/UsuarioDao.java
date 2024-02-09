package com.bancom.app.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bancom.app.demo.model.Usuario;

@Repository
public interface UsuarioDao extends CrudRepository<Usuario, Long> {

    com.bancom.app.demo.service.Usuario save(com.bancom.app.demo.service.Usuario usuario);

}
