package com.spring.springproject.controller;

import com.spring.springproject.dto.UserDto;
import com.spring.springproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.spring.springproject.controller.Constants.USERS_ID_URL;
import static com.spring.springproject.controller.Constants.USERS_URL;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping(USERS_URL)
    public Set<UserDto> findAll(){
       return userService.findAll();
    }

    @GetMapping(USERS_ID_URL)
    public UserDto findById(@PathVariable Integer id){
        return userService.findById(id);
    }

    @DeleteMapping(USERS_ID_URL)
    public void deleteById(@PathVariable Integer id){
        userService.delete(id);
    }

    @PostMapping(USERS_URL)
    public UserDto createUser(@RequestBody UserDto userDto){
        return userService.save(userDto);
    }

    @PutMapping(USERS_ID_URL)
    public void  editUser(@RequestBody UserDto userDto){
        userService.update(userDto);
    }


}
