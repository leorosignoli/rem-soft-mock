package br.com.remsoft.order.management.service.repositories.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Set;

@Entity
public class User {
  @Id private Long id;

  private String name;

  private String email;

  @OneToMany(mappedBy = "user")
  private Set<Order> orders;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public Set<Order> getOrders() {
    return orders;
  }

  public void setOrders(final Set<Order> orders) {
    this.orders = orders;
  }
}
