package br.com.remsoft.order.management.service.controllers.dtos.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record GetOrderResponseDTO(
    Long id,
    OffsetDateTime orderDate,
    BigDecimal totalAmount,
    UserDTO user,
    Set<OrderItemDTO> orderItems) {

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.CLASS,
      include = JsonTypeInfo.As.PROPERTY,
      property = "@class")
  public record UserDTO(Long id, String name, String email) {}

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.CLASS,
      include = JsonTypeInfo.As.PROPERTY,
      property = "@class")
  public record OrderItemDTO(Long id, Integer quantity, BigDecimal unitPrice, ProductDTO product) {}

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.CLASS,
      include = JsonTypeInfo.As.PROPERTY,
      property = "@class")
  public record ProductDTO(Long id, String name, BigDecimal price, String manufacturerName) {}
}
