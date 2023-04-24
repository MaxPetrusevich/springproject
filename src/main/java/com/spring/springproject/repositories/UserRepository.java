package com.spring.springproject.repositories;

import com.spring.springproject.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends CrudRepository<User,Integer>, PagingAndSortingRepository<User,Integer> {
}
