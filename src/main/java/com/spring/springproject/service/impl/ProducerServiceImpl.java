package com.spring.springproject.service.impl;


import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.entities.Producer;
import com.spring.springproject.repositories.ProducerRepository;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.service.interfaces.ProducerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProducerServiceImpl implements ProducerService {

    private final ModelMapper modelMapper;
    private final ProducerRepository repository;
    private final TechniqueRepository techniqueRepository;

    @Autowired
    public ProducerServiceImpl(ModelMapper modelMapper, ProducerRepository repository,
                               TechniqueRepository techniqueRepository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.techniqueRepository = techniqueRepository;
    }


    @Override
    public Set<ProducerDto> findAll() {
        return repository.findAll()
                .stream()
                .map(producer -> modelMapper.map(producer, ProducerDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public ProducerDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(producer -> modelMapper.map(producer, ProducerDto.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public ProducerDto save(ProducerDto object) {
        Producer producer = modelMapper.map(object, Producer.class);
        producer = repository.save(producer);
        return modelMapper.map(producer, ProducerDto.class);
    }
    @Transactional
    @Override
    public void update(ProducerDto object) {
        repository.update(modelMapper.map(object, Producer.class));
    }

    @Override
    public void delete(Integer id) {
        Producer producer = repository.findById(id).orElse(null);
        if (producer != null) {
            producer.getTechniques().forEach(technique -> {
                technique.setProducer(null);
                techniqueRepository.save(technique);
            });
            repository.deleteById(id);
        }
    }


    @Override
    public Page<ProducerDto> findAll(Pageable pageable, String name, String country) {
            Page<Producer> producerPage = repository.findAll(Producer.builder().name(name).country(country).build(), pageable);
            List<ProducerDto> producerDtoList = producerPage
                    .stream()
                    .map(producer -> modelMapper.map(producer, ProducerDto.class))
                    .toList();
            return new PageImpl<>(producerDtoList, pageable, producerPage.getTotalElements());

    }

    @Override
    @Transactional
    public void update(Integer id, String name, String country) {
        Producer producer = repository.findById(id).orElse(null);
        if (producer != null) {
            producer.setName(name);
            producer.setCountry(country);
            repository.update(producer);
        }
    }
    @Transactional
    @Override
    public ProducerDto save(String name, String country) {
        return modelMapper.map(repository.save(Producer.
                builder().name(name).country(country).build()), ProducerDto.class);
    }
}
