package com.spring.springproject.repositories;

import com.spring.springproject.entities.OrderTechnique;
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
public interface OrderTechniqueRepository extends JpaRepository<OrderTechnique, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO order_technique (order_id, tech_id, quantity) " +
            "VALUES (:#{#orderTechnique.order.id}, :#{#orderTechnique.technique.id}, :#{#orderTechnique.quantity})", nativeQuery = true)
    void saveOrderTechnique(@Param("orderTechnique") OrderTechnique orderTechnique);

    @Query("SELECT ot FROM OrderTechnique ot WHERE ot.id = :id")
    Optional<OrderTechnique> findById(@Param("id") Integer id);

    @Query("SELECT ot FROM OrderTechnique ot ORDER BY ot.id")
    List<OrderTechnique> findAll();

    @Query("UPDATE OrderTechnique ot SET ot.quantity = :#{#orderTechnique.quantity}, ot.order = :#{#orderTechnique.order}, ot.technique = :#{#orderTechnique.technique} WHERE ot.id = :#{#orderTechnique.id}")
    @Modifying
    @Transactional
    void update(@Param("orderTechnique") OrderTechnique orderTechnique);

    @Query("DELETE FROM OrderTechnique ot WHERE ot.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT ot FROM OrderTechnique ot " +
            "WHERE ot.quantity BETWEEN :minQuantity AND :maxQuantity " +
            "AND ot.order.amount BETWEEN :minOrderAmount AND :maxOrderAmount " +
            "AND LOWER(ot.order.user.userName) LIKE LOWER(CONCAT('%', :#{#orderTechnique.order.user.name}, '%')) " +
            "ORDER BY ot.id DESC",
            countQuery = "SELECT COUNT(ot) FROM OrderTechnique ot " +
                    "WHERE ot.quantity BETWEEN :minQuantity AND :maxQuantity " +
                    "AND ot.order.amount BETWEEN :minOrderAmount AND :maxOrderAmount " +
                    "AND LOWER(ot.order.user.userName) LIKE LOWER(CONCAT('%', :#{#orderTechnique.order.user.name}, '%'))")
    Page<OrderTechnique> findAll(@Param("orderTechnique") OrderTechnique orderTechnique,
                                 @Param("minQuantity") Integer minQuantity,
                                 @Param("maxQuantity") Integer maxQuantity,
                                 @Param("minOrderAmount") Double minOrderAmount,
                                 @Param("maxOrderAmount") Double maxOrderAmount,
                                 Pageable pageable);
}

