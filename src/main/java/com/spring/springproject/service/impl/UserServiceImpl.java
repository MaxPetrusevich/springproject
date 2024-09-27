package com.spring.springproject.service.impl;

import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.Role;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.RoleRepository;
import com.spring.springproject.repositories.UserRepository;
import com.spring.springproject.repositories.specifications.UserSpecifications;
import com.spring.springproject.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final int USER_ROLE_ID = 2;
    private final OrderServiceImpl orderService;
    private final ModelMapper modelMapper;
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final ImageServiceImpl imageService;
    private final OrderTechniqueServiceImpl orderTechniqueService;



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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        repository.saveUser(user);
        user.getRoles().add(roleRepository.findById(USER_ROLE_ID).orElse(null));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void update(UserDto object) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        object.setPassword(encoder.encode(object.getPassword()));
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
            user.getOrders().forEach(order->{
                order.getOrderTechniques()
                        .forEach(orderTechnique -> orderTechniqueService.deleteOrderTechniqueById(orderTechnique.getId()));
                orderService.deleteOrderById(order.getId());
            });

            repository.deleteById(id);
        }
    }
    @SneakyThrows
    private void uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            imageService.upload(image.getOriginalFilename(), image.getInputStream());
        }
    }
    @Transactional
    @Override
    public Optional<User> updateImage(Integer id, MultipartFile image) {

        return repository.findById(id)
                .map(entity -> {
                    uploadImage(image);
                    Optional.ofNullable(image)
                            .filter(Predicate.not(MultipartFile::isEmpty))
                            .ifPresent(avatar -> entity.setImage( "\\user\\" +avatar.getOriginalFilename()));
                    return entity;
                })
                .map(repository::saveAndFlush);
    }

    @Override
    public Optional<byte[]> findAvatar(Integer id) {
        return repository.findById(id)
                .map(User::getImage)
                .filter(StringUtils::hasText)
                .flatMap(imageService::get);
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable, String username, String name, String surname, String email) {
        User userSearch = User.builder()
                .userName(username)
                .email(email)
                .name(name)
                .surname(surname)
                .build();
        Page<User> userPage = repository.findAll(UserSpecifications.filterByUser(userSearch), pageable);
        return userPage.map(user->modelMapper.map(user, UserDto.class));
    }
    public Role getRole(String name){
        return roleRepository.findByName(name==null?"":name);
    }

    public User findBiyUserName(String name){
        return repository.findByUserName(name);
    }
}