package br.com.remsoft.order.management.service.repositories;

import br.com.remsoft.order.management.service.repositories.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository
    extends JpaRepository<Order, Long>, PagingAndSortingRepository<Order, Long> {

  Page<Order> findAll(Pageable pageable);
}