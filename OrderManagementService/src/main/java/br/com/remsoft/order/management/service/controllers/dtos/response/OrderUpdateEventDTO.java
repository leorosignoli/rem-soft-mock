package br.com.remsoft.order.management.service.controllers.dtos.response;

import java.time.OffsetDateTime;

public record OrderUpdateEventDTO(
    String eventType, Long orderId, OffsetDateTime timestamp, GetOrderResponseDTO orderData) {

  public enum EventType {
    ORDER_UPDATED
  }

  public static OrderUpdateEventDTO updated(GetOrderResponseDTO orderData) {
    return new OrderUpdateEventDTO(
        EventType.ORDER_UPDATED.name(), orderData.id(), OffsetDateTime.now(), orderData);
  }
}
