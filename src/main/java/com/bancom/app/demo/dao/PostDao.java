package com.bancom.app.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bancom.app.demo.model.Post;

@Repository
public interface PostDao extends CrudRepository<Post, Long> {

}
