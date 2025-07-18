package br.com.remsoft.order.management.service.controllers.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateOrderItemRequestDTO {
  @NotNull(message = "Product ID cannot be null")
  @Positive(message = "Product ID must be positive")
  private Long productId;

  @NotNull(message = "Quantity cannot be null")
  @Positive(message = "Quantity must be positive")
  private Integer quantity;

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
