package com.spring.springproject.service.impl;

import com.spring.springproject.dto.ModelDto;
import com.spring.springproject.entities.Model;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.specifications.ModelSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.ModelRepository;
import com.spring.springproject.service.interfaces.ModelService;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModelServiceImpl implements ModelService {

    private final ModelRepository repository;
    private final ModelMapper modelMapper;
    private final TechniqueRepository techniqueRepository;

    @Autowired
    public ModelServiceImpl(ModelRepository repository, ModelMapper modelMapper, TechniqueRepository techniqueRepository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.techniqueRepository = techniqueRepository;
    }


    @Override
    public Page<ModelDto> findAll(Pageable pageable, String name) {
        if (!StringUtils.isEmptyOrWhitespace(name)) {
            Page<Model> models = repository.findAll(ModelSpecification.searchModel(name), pageable);
            List<ModelDto> modelDtoList = models
                    .stream()
                    .map(model -> modelMapper.map(model, ModelDto.class))
                    .toList();
            return new PageImpl<>(modelDtoList, pageable, models.getTotalElements());
        } else {
            Page<Model> models = repository.findAll(pageable);
            List<ModelDto> modelDtoList = models
                    .stream()
                    .map(model -> modelMapper.map(model, ModelDto.class))
                    .toList();
            return new PageImpl<>(modelDtoList, pageable, models.getTotalElements());
        }
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
        Model model = repository.findById(id).orElse(null);
        if (model != null) {
            model.getTechniques().forEach(technique -> {
                technique.setModel(null);
                techniqueRepository.save(technique);
            });
            repository.deleteById(id);
        }
    }
}
