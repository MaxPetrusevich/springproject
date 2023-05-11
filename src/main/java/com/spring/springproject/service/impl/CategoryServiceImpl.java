package com.spring.springproject.service.impl;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.entities.Category;
import com.spring.springproject.service.interfaces.TypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.CategoryRepository;
import com.spring.springproject.service.interfaces.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final TypeService typeService;

    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, TypeService typeService, ModelMapper modelMapper) {
        this.repository = repository;
        this.typeService = typeService;
        this.modelMapper = modelMapper;
    }


    @Override
    public Set<CategoryDto> findByName(String name) {
        return repository.findByNameContaining(name).stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toSet());
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
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = null;
        if (categoryDto != null) {
            category = modelMapper.map(categoryDto, Category.class);
            category = repository.save(category);
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public void update(CategoryDto categoryDto) {
        repository.save(modelMapper.map(categoryDto, Category.class));
    }

    @Override
    public void delete(Integer id) {
        findById(id).getTypes().forEach(typeDto -> {
            typeDto.setCategory(null);
            typeService.save(typeDto);
        });
        repository.deleteById(id);
    }

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
        category = repository.save(modelMapper.map(categoryDto, Category.class));
        return modelMapper.map(category, CategoryDto.class);
    }


}
