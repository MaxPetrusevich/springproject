package com.spring.springproject.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MapperUtil {
    @Bean
    public ModelMapper getMapper(){
        return new ModelMapper();
    }

    public static <R,E> Set<R> convertSet(Set<E> set, Function<E, R> converter){
        return set.stream().map(e->converter.apply(e)).collect(Collectors.toSet());
    }
}
