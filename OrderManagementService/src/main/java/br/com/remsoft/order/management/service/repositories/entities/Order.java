package br.com.remsoft.order.management.service.repositories.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
public class Order {
  @Id private Long id;

  private OffsetDateTime orderDate;

  private BigDecimal totalAmount;

  @ManyToOne private User user;

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public OffsetDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(final OffsetDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(final BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public User getUser() {
    return user;
  }

  public void setUser(final User user) {
    this.user = user;
  }
}
