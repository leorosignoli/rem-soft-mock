package br.com.remsoft.order.management.service.repositories;

import br.com.remsoft.order.management.service.repositories.entities.Order;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository
    extends JpaRepository<Order, Long>, PagingAndSortingRepository<Order, Long> {

  @Query(
      "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH p.manufacturer LEFT JOIN FETCH o.user WHERE o.id = :id")
  Optional<Order> findByIdWithOrderItems(@Param("id") Long id);

  @Query(
      value =
          "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH p.manufacturer LEFT JOIN FETCH o.user",
      countQuery = "SELECT COUNT(o) FROM Order o")
  Page<Order> findAllWithOrderItems(Pageable pageable);

  Page<Order> findAll(Pageable pageable);

  @Query(
      value =
          "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH p.manufacturer LEFT JOIN FETCH o.user WHERE o.orderDate >= :since",
      countQuery = "SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :since")
  Page<Order> findRecentOrdersWithOrderItems(
      @Param("since") OffsetDateTime since, Pageable pageable);

  @Query(
      value =
          "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH p.manufacturer LEFT JOIN FETCH o.user WHERE o.totalAmount >= :minAmount",
      countQuery = "SELECT COUNT(o) FROM Order o WHERE o.totalAmount >= :minAmount")
  Page<Order> findHighValueOrdersWithOrderItems(
      @Param("minAmount") BigDecimal minAmount, Pageable pageable);

  @Query(
      value =
          "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH p.manufacturer LEFT JOIN FETCH o.user WHERE SIZE(o.orderItems) > 1",
      countQuery = "SELECT COUNT(o) FROM Order o WHERE SIZE(o.orderItems) > 1")
  Page<Order> findMultiItemOrdersWithOrderItems(Pageable pageable);
}
