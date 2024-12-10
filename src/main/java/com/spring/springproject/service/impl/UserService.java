package com.spring.springproject.service.impl;

import com.spring.springproject.dto.UserRegisterDto;
import com.spring.springproject.dto.UserUpdateDto;
import com.spring.springproject.entities.Citizen;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.RoleRepository;
import com.spring.springproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    public static final long USER_ROLE_ID = 2;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ImageServiceImpl imageService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final CitizenService citizenService;

    // Find all users
    public Set<User> findAll() {
        return Set.copyOf(userRepository.findAll());
    }

    // Для dashboard
    public long count() {
        return userRepository.count();
    }

    // Для списка пользователей с фильтрами
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable, String identifyNumber, Long roleId) {
        Specification<User> spec = (root, query, cb) -> {
            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
            
            if (identifyNumber != null && !identifyNumber.isEmpty()) {
                predicates.add(cb.like(root.get("identifyNumber"), "%" + identifyNumber + "%"));
            }
            if (roleId != null) {
                predicates.add(cb.equal(root.get("role").get("id"), roleId));
            }
            
            return predicates.isEmpty() ? null : 
                   cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        };
        return userRepository.findAll(spec, pageable);
    }

    // Find user by ID
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    // Save new user
    @Transactional
    public User save(User user) {
        if (user.getIdentifyNumber() == null || user.getIdentifyNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Identify number cannot be empty");
        }
        if (userRepository.existsByIdentifyNumber(user.getIdentifyNumber())) {
            throw new IllegalStateException("User with this identify number already exists");
        }
        return userRepository.save(user);
    }

    // Update existing user
    @Transactional
    public void update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        User existingUser = userRepository.findById(user.getId()).orElseThrow();
        existingUser.setIdentifyNumber(user.getIdentifyNumber());
        existingUser.setRole(user.getRole());
        if (user.getImage() != null) {
            existingUser.setImage(user.getImage());
        }
        userRepository.save(existingUser);
    }

    // Delete user by ID
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        if (user.getRole().getRoleName().equals("ADMIN")) {
            throw new IllegalStateException("Cannot delete admin user");
        }
        userRepository.deleteById(id);
    }

    // Upload user image (handle saving image to storage)
    @SneakyThrows
    private void uploadImage(MultipartFile image) {
        if (!image.isEmpty()) {
            imageService.upload(image.getOriginalFilename(), image.getInputStream());
        }
    }

    // Update user image
    @Transactional
    public Optional<User> updateImage(Long id, MultipartFile image) {
        return userRepository.findById(id)
                .map(entity -> {
                    if (image != null && !image.isEmpty()) {
                        try {
                            entity.setImage(image.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при обработке изображения", e);
                        }
                    }
                    return entity;
                })
                .map(userRepository::saveAndFlush);
    }

    // Find avatar by user ID
    public Optional<byte[]> findAvatar(Long id) {
        return userRepository.findById(id)
                .map(User::getImage)
                .filter(image -> image != null && image.length > 0);
    }

    // Find user by their identify number
    public User findByIdentifyNumber(String identifyNumber) {
        return userRepository.findByIdentifyNumber(identifyNumber);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public Optional<User> update(Long id, UserUpdateDto dto, MultipartFile image) {
        return userRepository.findById(id)
                .map(entity -> {
                    // Обновляем основные поля
                    if (StringUtils.hasText(dto.getPassword())) {
                        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }
                    
                    if (StringUtils.hasText(dto.getIdentifyNumber())) {
                        entity.setIdentifyNumber(dto.getIdentifyNumber());
                    }
                    
                    if (dto.getRoleId() != null) {
                        entity.setRole(roleService.findById(dto.getRoleId()));
                    }
                    
                    // Обновляем изображение если оно предоставлено
                    if (image != null && !image.isEmpty()) {
                        try {
                            entity.setImage(image.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при обработке изображения", e);
                        }
                    }
                    
                    return entity;
                })
                .map(userRepository::saveAndFlush);
    }

    @Transactional
    public void register(UserRegisterDto registerDto) {
        // Проверяем, не существует ли уже пользователь с таким ИИН
        if (findByIdentifyNumber(registerDto.getIdentifyNumber()) != null) {
            throw new RuntimeException("Пользователь с таким ИИН уже существует");
        }

        // Проверяем, не занят ли email
        if (citizenService.isEmailTaken(registerDto.getEmail())) {
            throw new RuntimeException("Email уже используется");
        }

        // Проверяем совпадение паролей
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }

        // Создаем пользователя
        User user = new User();
        user.setIdentifyNumber(registerDto.getIdentifyNumber());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(roleService.findByName("ROLE_USER"));
        user = userRepository.saveAndFlush(user);

        // Создаем гражданина и связываем с пользователем
        Citizen citizen = new Citizen();
        citizen.setFirstName(registerDto.getFirstName());
        citizen.setLastName(registerDto.getLastName());
        citizen.setMiddleName(registerDto.getMiddleName());
        citizen.setEmail(registerDto.getEmail());
        citizen.setPhone(registerDto.getPhone());
        citizen.setIdentifyNumber(registerDto.getIdentifyNumber());
        citizen.setPassportSeries(registerDto.getPassportSeries());
        citizen.setPassportNumber(registerDto.getPassportNumber());
        citizen.setAddress(registerDto.getAddress());
        citizen.setUser(user);
        
        citizenService.save(citizen);
    }

    public boolean isEmailTaken(String email) {
        return citizenService.isEmailTaken(email);
    }
}
