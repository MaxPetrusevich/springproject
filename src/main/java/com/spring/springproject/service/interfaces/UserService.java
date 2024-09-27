package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.Role;
import com.spring.springproject.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


public interface UserService extends Service<UserDto> {
    Optional<byte[]> findAvatar(Integer id);
    Optional<User> updateImage(Integer id, MultipartFile image);

    Page<UserDto> findAll(Pageable pageable, String username, String name, String surname, String email);
    Role getRole(String name);
    User findBiyUserName(String name);
}

