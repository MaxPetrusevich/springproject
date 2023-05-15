package com.spring.springproject.service.impl;

import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.RoleRepository;
import com.spring.springproject.repositories.UserRepository;
import com.spring.springproject.service.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    public static final int USER_ROLE_ID = 2;
    private final ModelMapper modelMapper;
    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository repository, RoleRepository roleRepository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public UserDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserDto save(UserDto object) {
        User user = modelMapper.map(object, User.class);
        user = repository.save(user);
        user.getRoles().add(roleRepository.findById(USER_ROLE_ID).orElse(null));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void update(UserDto object) {
        repository.save(modelMapper.map(object, User.class));
    }

    @Override
    public void delete(Integer id) {
        User user = repository.findById(id).orElse(null);
        if (user != null) {
            user.getRoles()
                    .forEach(role -> {
                        role.getUsers().remove(user);
                        roleRepository.save(role);
                    });
            repository.deleteById(id);
        }
    }
}