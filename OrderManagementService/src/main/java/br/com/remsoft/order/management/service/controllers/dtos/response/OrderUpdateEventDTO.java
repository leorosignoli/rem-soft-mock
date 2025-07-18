package br.com.remsoft.order.management.service.controllers.dtos.response;

import java.time.OffsetDateTime;

public record OrderUpdateEventDTO(
    String eventType, Long orderId, OffsetDateTime timestamp, GetOrderResponseDTO orderData) {

  public enum EventType {
    ORDER_CREATED,
    ORDER_UPDATED,
    ORDER_DELETED
  }

  public static OrderUpdateEventDTO created(GetOrderResponseDTO orderData) {
    return new OrderUpdateEventDTO(
        EventType.ORDER_CREATED.name(), orderData.id(), OffsetDateTime.now(), orderData);
  }

  public static OrderUpdateEventDTO updated(GetOrderResponseDTO orderData) {
    return new OrderUpdateEventDTO(
        EventType.ORDER_UPDATED.name(), orderData.id(), OffsetDateTime.now(), orderData);
  }

  public static OrderUpdateEventDTO deleted(Long orderId) {
    return new OrderUpdateEventDTO(
        EventType.ORDER_DELETED.name(), orderId, OffsetDateTime.now(), null);
  }
}
