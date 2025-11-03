package com.laxamana.facebookapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Single-package controller exposing a simple REST API for posts.
 * AS
 * Endpoints:
 * - POST   /api/posts         -> create a post
 * - GET    /api/posts         -> list all posts
 * - GET    /api/posts/{id}    -> get post by id
 * - PUT    /api/posts/{id}    -> update post (partial fields supported)
 * - DELETE /api/posts/{id}    -> delete post
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository repository;

    public PostController(PostRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        if (post.getAuthor() == null || post.getAuthor().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Post saved = repository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Post> listPosts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updates) {
        return repository.findById(id).map(existing -> {
            if (updates.getAuthor() != null) existing.setAuthor(updates.getAuthor());
            if (updates.getContent() != null) existing.setContent(updates.getContent());
            if (updates.getImageUrl() != null) existing.setImageUrl(updates.getImageUrl());
            Post saved = repository.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

