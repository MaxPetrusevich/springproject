package com.spring.springproject.service.impl;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.entities.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.ModelRepository;
import com.spring.springproject.service.interfaces.ModelService;

import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<ModelDto> findByName(String name) {
        return repository.findByNameContaining(name)
                .stream()
                .map(model -> modelMapper.map(model, ModelDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public void update(Integer id, String name) {
        Model model = repository.findById(id).orElse(null);
        if (model != null) {
            model.setName(name);
            repository.save(model);
        }
    }

    @Override
    public ModelDto save(String name) {
        Model model = new Model();
        model.setName(name);
        model = repository.save(model);
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public Set<ModelDto> findAll() {
        return repository.findAll()
                .stream()
                .map(model -> modelMapper.map(model, ModelDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public ModelDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(model -> modelMapper.map(model, ModelDto.class))
                .findFirst()
                .orElse(null);
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
