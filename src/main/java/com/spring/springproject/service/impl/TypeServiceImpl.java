package com.spring.springproject.service.impl;

import com.spring.springproject.dto.TypeDto;

import com.spring.springproject.entities.Type;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.TypeRepository;
import com.spring.springproject.service.interfaces.TypeService;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TypeServiceImpl implements TypeService {
    private final ModelMapper modelMapper;
    private final TypeRepository repository;

    @Autowired
    public TypeServiceImpl(ModelMapper modelMapper, TypeRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

    @Override
    public Set<TypeDto> findAll() {
        Set<TypeDto> typeDtoSet = new HashSet<>();
        for (Type type:
             repository.findAll()) {
            typeDtoSet.add(modelMapper.map(type, TypeDto.class));
        }
        return typeDtoSet;
    }

    @Override
    public TypeDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), TypeDto.class);
    }

    @Override
    public TypeDto save(TypeDto object) {
        Type type = modelMapper.map(object, Type.class);
        type = repository.save(type);
        return modelMapper.map(type, TypeDto.class);
    }

    @Override
    public void update(TypeDto object) {
        repository.save(modelMapper.map(object, Type.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public Set<TypeDto> findByName(String name) {
        Set<TypeDto> typeDtoSet = new HashSet<>();
        for (Type type:
                repository.findByNameContaining(name)) {
            typeDtoSet.add(modelMapper.map(type, TypeDto.class));
        }
        return typeDtoSet;
    }
}
