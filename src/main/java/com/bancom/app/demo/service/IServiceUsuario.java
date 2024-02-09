package com.bancom.app.demo.service;

import java.util.List;

import com.bancom.app.demo.model.Usuario;

public interface IServiceUsuario {
    public Usuario findById(Long id);
    public List<Usuario> findAll();
    public Usuario save(Usuario usuario);
    public void delete(Long id);
}
