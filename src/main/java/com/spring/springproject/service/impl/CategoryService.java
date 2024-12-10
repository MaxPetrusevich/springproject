package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Category;
import com.spring.springproject.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable, String name) {
        if (name != null && !name.isEmpty()) {
            return categoryRepository.findByCategoryContainingIgnoreCase(name, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public Category save(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        Category category = Category.builder()
                .category(categoryName.trim())
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public void update(Long id, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        Category category = findById(id);
        category.setCategory(categoryName.trim());
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        if (category.getServices() != null && !category.getServices().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing services");
        }
        categoryRepository.deleteById(id);
    }
}
