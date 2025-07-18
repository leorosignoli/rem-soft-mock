package br.com.remsoft.order.management.service.controllers.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class CreateOrderRequestDTO {
  @NotNull(message = "User ID cannot be null")
  @Positive(message = "User ID must be positive")
  private Long userId;

  @NotEmpty(message = "Order items cannot be empty")
  @Valid
  private List<CreateOrderItemRequestDTO> orderItems;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<CreateOrderItemRequestDTO> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<CreateOrderItemRequestDTO> orderItems) {
    this.orderItems = orderItems;
  }
}
