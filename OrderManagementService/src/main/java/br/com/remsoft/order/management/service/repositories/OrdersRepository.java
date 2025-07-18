package br.com.remsoft.order.management.service.repositories;

import br.com.remsoft.order.management.service.repositories.entities.Order;
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
}
