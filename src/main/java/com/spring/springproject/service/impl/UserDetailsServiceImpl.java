package com.spring.springproject.service.impl;

import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifyNumber) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifyNumber(identifyNumber);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + identifyNumber);
        }
        
        return new org.springframework.security.core.userdetails.User(
            user.getIdentifyNumber(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()))
        );
    }
}
