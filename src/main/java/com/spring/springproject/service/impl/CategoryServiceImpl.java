package com.spring.springproject.service.impl;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.entities.Category;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.CategoryRepository;
import com.spring.springproject.service.interfaces.CategoryService;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    private final ModelMapper modelMapper;
    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Set<CategoryDto> findAll() {
        Set<CategoryDto> categoryDtoSet = new HashSet<>();
        for (Category category:
             repository.findAll()) {
            categoryDtoSet.add(modelMapper.map(category, CategoryDto.class));
        }
        return categoryDtoSet;
    }

    @Override
    public CategoryDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        category = repository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public void update(CategoryDto categoryDto) {
        repository.save(modelMapper.map(categoryDto, Category.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
