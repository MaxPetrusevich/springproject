package com.spring.springproject.repositories;

import com.spring.springproject.entities.Technique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechniqueRepository extends JpaRepository<Technique, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Technique (price, producer_id, model_id, category_id) " +
            "VALUES (:#{#technique.price}, :#{#technique.producer.id}, :#{#technique.model.id}, :#{#technique.category.id})", nativeQuery = true)
    void saveTechnique(@Param("technique") Technique technique);

    @Query("SELECT t FROM Technique t WHERE t.id = :id")
    Optional<Technique> findById(@Param("id") Integer id);

    @Query("SELECT t FROM Technique t ORDER BY t.id")
    List<Technique> findAll();

    @Query("UPDATE Technique t SET t.price = :#{#technique.price}, t.producer = :#{#technique.producer}, " +
            "t.model = :#{#technique.model}, t.category = :#{#technique.category} WHERE t.id = :#{#technique.id}")
    @Modifying
    @Transactional
    void update(@Param("technique") Technique technique);

    @Query("DELETE FROM Technique t WHERE t.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT t FROM Technique t WHERE t.price BETWEEN :startPrice AND :endPrice " +
            "AND LOWER(t.producer.name) LIKE LOWER(CONCAT('%', :#{#technique.producer.name}, '%')) " +
            "AND LOWER(t.model.name) LIKE LOWER(CONCAT('%', :#{#technique.model.name}, '%')) " +
            "AND LOWER(t.category.name) LIKE LOWER(CONCAT('%', :#{#technique.category.name}, '%')) ORDER BY t.id DESC ",
            countQuery = "SELECT COUNT(t) FROM Technique t WHERE t.price BETWEEN :startPrice AND :endPrice " +
                    "AND LOWER(t.producer.name) LIKE LOWER(CONCAT('%', :#{#technique.producer.name}, '%')) " +
                    "AND LOWER(t.model.name) LIKE LOWER(CONCAT('%', :#{#technique.model.name}, '%')) " +
                    "AND LOWER(t.category.name) LIKE LOWER(CONCAT('%', :#{#technique.category.name}, '%'))")
    Page<Technique> findAll(@Param("technique") Technique technique, @Param("startPrice") Double startPrice, @Param("endPrice") Double endPrice, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO tech_store (tech_id, store_id) " +
            "VALUES (:techId, :storeId)", nativeQuery = true)
    void saveStoreTech(@Param("storeId")Integer storeId, @Param("techId")Integer techId );
}
