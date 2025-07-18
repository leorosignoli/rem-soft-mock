package br.com.remsoft.order.management.service.controllers.dtos.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record GetOrderResponseDTO(
    Long id,
    OffsetDateTime orderDate,
    BigDecimal totalAmount,
    UserDTO user,
    Set<OrderItemDTO> orderItems) {

  public record UserDTO(Long id, String name, String email) {}

  public record OrderItemDTO(Long id, Integer quantity, BigDecimal unitPrice, ProductDTO product) {}

  public record ProductDTO(Long id, String name, BigDecimal price, String manufacturerName) {}
}
