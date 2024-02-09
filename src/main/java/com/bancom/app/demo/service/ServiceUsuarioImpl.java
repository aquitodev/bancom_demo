package com.bancom.app.demo.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancom.app.demo.dao.UsuarioDao;
import com.bancom.app.demo.model.Usuario;

@Service
public class ServiceUsuarioImpl implements IServiceUsuario {

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return (List<Usuario>) this.usuarioDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return this.usuarioDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario == null) return null;

        Usuario usuarioSaved = this.usuarioDao.save(usuario);
        return usuarioSaved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.usuarioDao.deleteById(id);
    }
}
