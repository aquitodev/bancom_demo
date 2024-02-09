package com.bancom.app.demo.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancom.app.demo.dao.PostDao;
import com.bancom.app.demo.model.Post;

@Service
public class ServicePostImpl implements IServicePost {

    @Autowired
    private PostDao postDao;

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return (List<Post>) this.postDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return this.postDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Post save(Post post) {
        if (post == null) return null;

        Post postSaved = this.postDao.save(post);
        return postSaved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.postDao.deleteById(id);
    }
    
}
