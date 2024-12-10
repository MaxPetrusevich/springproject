package com.spring.springproject.service.impl;

import com.spring.springproject.dto.CitizenRequestDto;
import com.spring.springproject.entities.Citizen;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.CitizenRepository;
import com.spring.springproject.repositories.UserRepository;
import com.spring.springproject.service.impl.specifications.CitizenSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CitizenService {

    private final CitizenRepository citizenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;

    // Find all Citizens with pagination and optional filters
    public Page<Citizen> findAll(Pageable pageable, String firstName, String lastName,
                                 String passportNumber, String phone, String identifyNumber) {
        Page<Citizen> citizens = citizenRepository.findAll(
                CitizenSpecification.filterCitizens(firstName, lastName, passportNumber, phone, identifyNumber), pageable);
        return new PageImpl<>(citizens.getContent(), pageable, citizens.getTotalElements());
    }
 // Для dashboard
 public long count() {
    return citizenRepository.count();
}

  // Для списка граждан с фильтрами
  public Page<Citizen> findAll(Pageable pageable, String lastName, String firstName, String identifyNumber) {
    return citizenRepository.findAll(
        CitizenSpecification.filterCitizens(firstName, lastName, null, null, identifyNumber),
        pageable
    );
}
    // Find all Citizens without pagination
    public Set<Citizen> findAll() {
        return new HashSet<>(citizenRepository.findAll());
    }

    // Find a Citizen by ID
    public Citizen findById(Long id) {
        return citizenRepository.findById(id).orElse(null);
    }

    // Find a Citizen by Identify Number
    public Citizen findByIdentifyNumber(String identifyNumber) {
        return citizenRepository.findByIdentifyNumber(identifyNumber);
    }

    // Save a new Citizen
    public Citizen save(Citizen citizen) {
        return citizenRepository.saveAndFlush(citizen);
    }

    // Update an existing Citizen
    @Transactional
    public void update(Citizen citizen) {
        citizenRepository.save(citizen); // Save will handle both create and update operations
    }

    // Delete a Citizen by ID
    public void delete(Long id) {
        citizenRepository.deleteById(id);
    }

    // Save a new Citizen with necessary fields
    @Transactional
    public Citizen save(String firstName, String lastName, String middleName, String phone,
                        String email, String identifyNumber, String passportSeries,
                        String passportNumber, String address, User user) {
        Citizen citizen = Citizen.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .phone(phone)
                .email(email)
                .identifyNumber(identifyNumber)
                .passportSeries(passportSeries)
                .passportNumber(passportNumber)
                .address(address)
                .user(user)
                .build();
        return citizenRepository.save(citizen);
    }

    public boolean isEmailTaken(String email) {
        return citizenRepository.existsByEmail(email);
    }

    @Transactional
    public void register(CitizenRequestDto dto) {
        // Создаем пользователя
        User user = new User();
        user.setIdentifyNumber(dto.getIdentifyNumber());
        user.setPassword(passwordEncoder.encode(dto.getIdentifyNumber())); // Используем ИИН как начальный пароль
        user.setRole(roleService.findByName("USER"));
        user = userRepository.save(user);

        // Создаем гражданина
        Citizen citizen = new Citizen();
        citizen.setFirstName(dto.getFirstName());
        citizen.setLastName(dto.getLastName());
        citizen.setMiddleName(dto.getMiddleName());
        citizen.setPhone(dto.getPhone());
        citizen.setEmail(dto.getEmail());
        citizen.setIdentifyNumber(dto.getIdentifyNumber());
        citizen.setPassportSeries(dto.getPassportSeries());
        citizen.setPassportNumber(dto.getPassportNumber());
        citizen.setAddress(dto.getAddress());
        citizen.setUser(user);

        citizenRepository.save(citizen);
    }

    public Citizen findByEmail(String email) {
        return citizenRepository.findByEmail(email);
    }

    public Citizen findByUser(User user) {
        return citizenRepository.findByUser(user)
            .orElseThrow(() -> new EntityNotFoundException("Гражданин не найден"));
    }
}
