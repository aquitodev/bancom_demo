package com.bancom.app.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bancom.app.demo.entities.Post;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

}
