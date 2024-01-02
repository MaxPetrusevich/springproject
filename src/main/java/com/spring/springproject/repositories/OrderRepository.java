package com.spring.springproject.repositories;

import com.spring.springproject.entities.Order;
import com.spring.springproject.entities.Status;
import com.spring.springproject.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO orders (order_amount, user_id, order_date, status) VALUES (:#{#order.amount}, :#{#order.user.id}, :#{#order.orderDate}, :#{#order.status.name()})", nativeQuery = true)
    void saveOrder(@Param("order") Order order);

    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findById(@Param("id") Integer id);

    @Query("SELECT o FROM Order o ORDER BY o.id")
    Page<Order> findAll(Pageable pageable);

    @Query("UPDATE Order o SET o.amount = :#{#order.amount}, o.user = :#{#order.user}, o.orderDate = :#{#order.orderDate}, o.status = :#{#order.status} WHERE o.id = :#{#order.id}")
    @Modifying
    @Transactional
    void update(@Param("order") Order order);

    @Query("DELETE FROM Order o WHERE o.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);

    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user = :user")
    Page<Order> findAllByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user = :user")
    List<Order> findAllByUser(@Param("user") User user);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :lastMonth")
    Page<Order> findAllInLastMonth(@Param("lastMonth") LocalDate lastMonth, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = 'BUCKET' and o.user.id = :userId")
    Optional<Order> getBucket(@Param("userId") Integer userId);
}
