package com.spring.springproject.service.impl;

import com.spring.springproject.dto.CategoryDto;
import com.spring.springproject.dto.TypeDto;
import com.spring.springproject.entities.Category;
import com.spring.springproject.repositories.TypeRepository;
import com.spring.springproject.service.interfaces.TypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.CategoryRepository;
import com.spring.springproject.service.interfaces.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        Set<CategoryDto> categoryDtoSet = new HashSet<>();
        for (Category category :
                repository.findByNameContaining(name)) {
            categoryDtoSet.add(modelMapper.map(category, CategoryDto.class));
        }
        return categoryDtoSet;
    }

    @Override
    public Set<CategoryDto> findAll() {
        Set<CategoryDto> categoryDtoSet = new HashSet<>();
        for (Category category :
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
        categoryDto.getTypes().stream()
                .forEach(typeDto -> {
                    typeDto.setCategory(null);
                    typeService.update(typeDto);
                });
        if (categoryDto.getTypes() != null) {
            categoryDto.getTypes().removeAll(categoryDto.getTypes());
        }
        CategoryDto finalCategoryDto = categoryDto;
        Arrays.stream(typeIdes)
                .map(id -> typeService.findById(id))
                .forEach(typeDto -> {
                    typeDto.setCategory(finalCategoryDto);
                    typeService.update(typeDto);
                    finalCategoryDto.getTypes().add(typeDto);
                });
        category = repository.save(modelMapper.map(categoryDto, Category.class));
        return modelMapper.map(category, CategoryDto.class);

    }
}
