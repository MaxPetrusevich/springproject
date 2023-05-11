package com.spring.springproject.service.impl;


import com.spring.springproject.dto.ProducerDto;
import com.spring.springproject.entities.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.ProducerRepository;
import com.spring.springproject.service.interfaces.ProducerService;

import java.util.Set;
import java.util.stream.Collectors;

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
        return repository.findByNameContaining(name)
                .stream()
                .map(producer -> modelMapper.map(producer, ProducerDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ProducerDto> findByCountry(String country) {
        return repository.findByCountryContaining(country)
                .stream()
                .map(producer -> modelMapper.map(producer, ProducerDto.class))
                .collect(Collectors.toSet());
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
        return modelMapper.map(repository.save(Producer.
                builder().name(name).country(country).build()), ProducerDto.class);
    }
}
