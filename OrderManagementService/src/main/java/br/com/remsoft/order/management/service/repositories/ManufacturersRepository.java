package br.com.remsoft.order.management.service.repositories;

import br.com.remsoft.order.management.service.repositories.entities.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturersRepository extends JpaRepository<Manufacturer, Long> {}
