package com.spring.springproject.repositories;

import com.spring.springproject.entities.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OrderBy;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Model (model_name) VALUES (:#{#model.name})", nativeQuery = true)
    void saveModel(@Param("model") Model model);

    @Query("SELECT m FROM Model m WHERE m.id = :id")
    Optional<Model> findById(@Param("id") Integer id);
    @OrderBy("id")
    @Query("SELECT m FROM Model m ORDER BY m.id")
    List<Model> findAll();

    @Query("UPDATE Model m SET m.name = :#{#model.name} WHERE m.id = :#{#model.id}")
    @Modifying
    @Transactional
    void update(@Param("model") Model model);

    @Query("DELETE FROM Model m WHERE m.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);
    @Query(value = "SELECT m FROM Model m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :#{#model.name}, '%')) order by m.id DESC ",
            countQuery = "SELECT COUNT(m) FROM Model m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :#{#model.name}, '%'))")
    Page<Model> findAll(@Param("model") Model model, Pageable pageable);
}
