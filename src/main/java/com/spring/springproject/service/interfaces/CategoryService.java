package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.CategoryDto;

import java.util.Set;

public interface CategoryService extends Service<CategoryDto>{
    Set<CategoryDto> findByName(String name);
}
