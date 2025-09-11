package com.gigatownpartners.dashboard.services;

import com.gigatownpartners.dashboard.dtos.BlogDto;
import com.gigatownpartners.dashboard.dtos.PagedResponseDto;
import com.gigatownpartners.dashboard.entities.Blog;
import com.gigatownpartners.dashboard.repositories.BlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public Blog retrieve(Long id) {
        return blogRepository.findById(id).orElseThrow();
    }

    public Blog create(BlogDto input) {
        Blog blog = new Blog(input.getTitle(), input.getHtmlContent());

        return blogRepository.save(blog);
    }

    public PagedResponseDto<Blog> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Blog> blogPostPage = blogRepository.findAll(pageable);
        return new PagedResponseDto<>(
                blogPostPage.getContent(),
                blogPostPage.getNumber(),
                blogPostPage.getSize(),
                blogPostPage.getTotalElements(),
                blogPostPage.getTotalPages(),
                blogPostPage.isLast()
        );
    }
}
