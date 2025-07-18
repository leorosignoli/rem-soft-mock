package br.com.remsoft.order.management.service.repositories.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Set;

@Entity
public class Manufacturer {

  @Id private Long id;

  private String name;

  @OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
  private Set<Product> products;

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Set<Product> getProducts() {
    return products;
  }

  public void setProducts(final Set<Product> products) {
    this.products = products;
  }
}
