package com.bancom.app.demo.service;

import java.util.List;

import com.bancom.app.demo.model.Post;

public interface IServicePost {
    public Post findById(Long id);
    public List<Post> findAll();
    public Post save(Post post);
    public void delete(Long id);
}
