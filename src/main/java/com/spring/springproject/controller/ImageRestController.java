package com.spring.springproject.controller;

import com.spring.springproject.service.interfaces.TechniqueService;
import com.spring.springproject.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequiredArgsConstructor
public class ImageRestController {

    private final UserService userService;
    private final TechniqueService techniqueService;


    @GetMapping(value = "/user/{id}/avatar")
    public ResponseEntity<byte[]> findAvatar(@PathVariable("id") Integer id) {
        return userService.findAvatar(id)
                .map(content -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentLength(content.length)
                        .body(content))
                .orElseGet(notFound()::build);
    }
    @GetMapping(value = "/tech/{id}/avatar")
    public ResponseEntity<byte[]> findTechAvatar(@PathVariable("id") Integer id) {
        return techniqueService.findAvatar(id)
                .map(content -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentLength(content.length)
                        .body(content))
                .orElseGet(notFound()::build);
    }
}
