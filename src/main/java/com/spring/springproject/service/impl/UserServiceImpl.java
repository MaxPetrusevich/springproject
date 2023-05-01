package com.spring.springproject.service.impl;

import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.springproject.repositories.UserRepository;
import com.spring.springproject.service.interfaces.UserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

    @Override
    public Set<UserDto> findAll() {
        Set<UserDto> userDtoSet = new HashSet<>();
        for (User user :
                repository.findAll()) {
            userDtoSet.add(modelMapper.map(user, UserDto.class));
        }
        return userDtoSet;
    }

    @Override
    public UserDto findById(Integer id) {
        return modelMapper.map(repository.findById(id).orElse(null), UserDto.class);
    }

    @Override
    public UserDto save(UserDto object) {
        User user = modelMapper.map(object, User.class);
        user = repository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void update(UserDto object) {
        repository.save(modelMapper.map(object, User.class));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}