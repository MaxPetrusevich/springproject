package com.spring.springproject.service.impl;


import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.entities.Producer;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.ProducerRepository;
import com.spring.springproject.service.interfaces.ProducerService;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class ProducerServiceImpl implements ProducerService {

    private final ModelMapper modelMapper;
    private final ProducerRepository repository;

    @Autowired
    public ProducerServiceImpl(ModelMapper modelMapper, ProducerRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

    @Override
    public Set<ProducerDto> findAll() {
        Set<ProducerDto> producerDtoSet = new HashSet<>();
        for (Producer producer :
                repository.findAll()) {
            producerDtoSet.add(modelMapper.map(producer, ProducerDto.class));
        }
        return producerDtoSet;
    }

    @Override
    public ProducerDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), ProducerDto.class);
    }

    @Override
    public ProducerDto save(ProducerDto object) {
        Producer producer = modelMapper.map(object, Producer.class);
        producer = repository.save(producer);
        return modelMapper.map(producer, ProducerDto.class);
    }

    @Override
    public void update(ProducerDto object) {
        repository.save(modelMapper.map(object, Producer.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public Set<ProducerDto> findByName(String name) {
        Set<ProducerDto> producerDtoSet = new HashSet<>();
        for (Producer producer :
                repository.findByNameContaining(name)) {
            producerDtoSet.add(modelMapper.map(producer, ProducerDto.class));
        }
        return producerDtoSet;
    }

    @Override
    public Set<ProducerDto> findByCountry(String country) {
        Set<ProducerDto> producerDtoSet = new HashSet<>();
        for (Producer producer :
                repository.findByCountryContaining(country)) {
            producerDtoSet.add(modelMapper.map(producer, ProducerDto.class));
        }
        return producerDtoSet;
    }

    @Override
    public void update(Integer id, String name, String country) {
        Producer producer = repository.findById(id).orElse(null);
        if (producer != null) {
            producer.setName(name);
            producer.setCountry(country);
            repository.save(producer);
        }
    }

    @Override
    public ProducerDto save(String name, String country) {
        return null;
    }
}
