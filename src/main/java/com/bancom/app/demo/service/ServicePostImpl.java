package com.bancom.app.demo.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancom.app.demo.repository.PostRepository;
import com.bancom.app.demo.entities.Post;

@Service
public class ServicePostImpl implements IServicePost {

    @Autowired
    private PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return (List<Post>) this.postRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Post findById(Long id) {
        if (id == null) return null;
        return this.postRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Post save(Post post) {
        if (post == null) return null;

        Post postSaved = this.postRepository.save(post);
        return postSaved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Post post = this.findById(id);
        if (post == null) return;
        
        post.setActive(false);
        this.postRepository.save(post);
    }
}
