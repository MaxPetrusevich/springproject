package com.spring.springproject.service.impl;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.entities.Category;
import com.spring.springproject.repositories.CategoryRepository;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.service.interfaces.CategoryService;
import com.spring.springproject.service.interfaces.TypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final TypeService typeService;
    private final ModelMapper modelMapper;
    private final TechniqueRepository techniqueRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, TypeService typeService, ModelMapper modelMapper, TechniqueRepository techniqueRepository) {
        this.repository = repository;
        this.typeService = typeService;
        this.modelMapper = modelMapper;
        this.techniqueRepository = techniqueRepository;
    }


    @Override
    public Set<CategoryDto> findAll() {
        return repository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public CategoryDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = null;
        if (categoryDto != null) {
            category = modelMapper.map(categoryDto, Category.class);
            repository.saveCategory(category);
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    @Transactional
    public void update(CategoryDto categoryDto) {
        repository.update(modelMapper.map(categoryDto, Category.class));
    }

    @Override
    public void delete(Integer id) {
        Category category = repository.findById(id).orElse(null);
        if (category != null) {
            category.getTechniques().forEach(technique -> {
                technique.setCategory(null);
                techniqueRepository.save(technique);
            });
            category.getTypes().forEach(type -> {
                type.setCategory(null);
                typeService.save(modelMapper.map(type, TypeDto.class));
            });
            repository.deleteById(id);
        }
    }

    @Override
    public Page<CategoryDto> findAll(Pageable pageable, String name) {
            Page<Category> categories = repository.findAll(Category.builder().name(name).build(), pageable);
            List<CategoryDto> categoryDtoList = categories
                    .stream()
                    .map(category -> modelMapper.map(category, CategoryDto.class))
                    .toList();
            return new PageImpl<>(categoryDtoList, pageable, categories.getTotalElements());
    }

    @Override
    @Transactional
    public void update(String name, Integer[] typeIdes, CategoryDto categoryDto) {
        categoryDto.setName(name);
        categoryDto.getTypes()
                .forEach(typeDto -> {
                    typeDto.setCategory(null);
                    typeService.update(typeDto);
                });
        if (categoryDto.getTypes() != null) {
            categoryDto.getTypes().removeAll(categoryDto.getTypes());
        }
        CategoryDto finalCategoryDto = categoryDto;
        Arrays.stream(typeIdes)
                .map(typeService::findById)
                .forEach(typeDto -> {
                    typeDto.setCategory(finalCategoryDto);
                    typeService.update(typeDto);
                    finalCategoryDto.getTypes().add(typeDto);
                });
        repository.update(modelMapper.map(finalCategoryDto, Category.class));
    }

    @Transactional
    @Override
    public CategoryDto save(String name, Integer[] typeIdes, CategoryDto categoryDto) {
        categoryDto.setName(name);
        Category category = repository.save(modelMapper.map(categoryDto, Category.class));
        categoryDto = modelMapper.map(category, CategoryDto.class);
        categoryDto.getTypes()
                .forEach(typeDto -> {
                    typeDto.setCategory(null);
                    typeService.update(typeDto);
                });
        if (categoryDto.getTypes() != null) {
            categoryDto.getTypes().removeAll(categoryDto.getTypes());
        }
        CategoryDto finalCategoryDto = categoryDto;
        Arrays.stream(typeIdes)
                .map(typeService::findById)
                .forEach(typeDto -> {
                    typeDto.setCategory(finalCategoryDto);
                    typeService.update(typeDto);
                    finalCategoryDto.getTypes().add(typeDto);
                });
        repository.update(modelMapper.map(categoryDto, Category.class));
        return modelMapper.map(category, CategoryDto.class);
    }


}
