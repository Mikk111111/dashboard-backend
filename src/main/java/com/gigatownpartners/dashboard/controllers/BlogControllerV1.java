package com.gigatownpartners.dashboard.controllers;

import com.gigatownpartners.dashboard.dtos.BlogDto;
import com.gigatownpartners.dashboard.dtos.PagedResponseDto;
import com.gigatownpartners.dashboard.entities.Blog;
import com.gigatownpartners.dashboard.services.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/blogs")
@RestController
public class BlogControllerV1 {
    private final BlogService blogService;

    public BlogControllerV1(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("")
    public ResponseEntity<Blog> create(@RequestBody BlogDto input) {
        Blog blog = blogService.create(input);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> retrieve(@PathVariable Long id) {
        Blog blog = blogService.retrieve(id);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("")
    public ResponseEntity<PagedResponseDto<Blog>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        final int MAX_PAGE_SIZE = 50;

        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        PagedResponseDto<Blog> blogPosts = blogService.findAll(page, size);
        return ResponseEntity.ok(blogPosts);
    }
}
