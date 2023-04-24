package com.spring.springproject.service.impl;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.entities.Model;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.ModelRepository;
import com.spring.springproject.service.interfaces.ModelService;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class ModelServiceImpl implements ModelService {

    private final ModelRepository repository;
    private final ModelMapper modelMapper;
    @Autowired
    public ModelServiceImpl(ModelRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Set<ModelDto> findAll() {
        Set<ModelDto> modelDtoSet = new HashSet<>();
        for (Model model:
                repository.findAll()) {
            modelDtoSet.add(modelMapper.map(model, ModelDto.class));
        }
        return modelDtoSet;
    }

    @Override
    public ModelDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), ModelDto.class);
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        Model model = modelMapper.map(modelDto, Model.class);
        model = repository.save(model);
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public void update(ModelDto modelDto) {
        repository.save(modelMapper.map(modelDto, Model.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
