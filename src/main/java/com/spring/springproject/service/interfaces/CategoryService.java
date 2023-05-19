package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService extends Service<CategoryDto> {

    Page<CategoryDto> findAll(Pageable pageable, String name);

    CategoryDto save(String name, Integer[] typeIdes, CategoryDto categoryDto);

}
