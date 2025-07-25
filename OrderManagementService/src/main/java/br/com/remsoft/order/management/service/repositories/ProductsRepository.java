package br.com.remsoft.order.management.service.repositories;

import br.com.remsoft.order.management.service.repositories.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {}
