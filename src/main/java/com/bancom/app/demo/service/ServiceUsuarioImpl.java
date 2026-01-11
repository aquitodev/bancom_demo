package com.bancom.app.demo.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancom.app.demo.repository.UsuarioRepository;
import com.bancom.app.demo.entities.Usuario;

@Service
public class ServiceUsuarioImpl implements IServiceUsuario {

    @Autowired
    private UsuarioRepository usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return (List<Usuario>) this.usuarioDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        if (id == null) return null;
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
        Usuario usuario = this.findById(id);
        if (usuario == null) return;

        usuario.setActive(false);
        this.usuarioDao.save(usuario);
    }

    @Override
    public Usuario login(Usuario usuario) {
        return this.usuarioDao.login(usuario.getNickname(), usuario.getPassword());
    }

    @Override
    public Usuario findByNickname(String nickname) {
        return this.usuarioDao.findByNickname(nickname).orElse(null);
    }
}
