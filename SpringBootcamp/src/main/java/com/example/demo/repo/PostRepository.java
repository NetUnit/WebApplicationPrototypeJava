package com.example.demo.repo;

import com.example.demo.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
    // * necessary methods has been inherited from CrudRepository
    // redefine parental class if wanted to alter changes
}
