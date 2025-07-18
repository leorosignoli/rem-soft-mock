package br.com.remsoft.order.management.service.listeners;

import br.com.remsoft.order.management.service.controllers.dtos.response.OrderUpdateEventDTO;
import br.com.remsoft.order.management.service.services.OrderUpdateService;
import br.com.remsoft.order.management.service.services.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCacheEvictionListener implements MessageListener {

  private static final Logger logger = LoggerFactory.getLogger(OrderCacheEvictionListener.class);

  private final OrdersService ordersService;
  private final OrderUpdateService orderUpdateService;

  public OrderCacheEvictionListener(
      OrdersService ordersService, OrderUpdateService orderUpdateService) {
    this.ordersService = ordersService;
    this.orderUpdateService = orderUpdateService;
  }

  @Override
  public void onMessage(final Message message, final byte[] pattern) {
    final String key = new String(message.getBody());
    final String channel = new String(message.getChannel());

    logger.info("Received Redis keyspace notification: channel={}, key={}", channel, key);

    if (key.startsWith("orders::")) {
      handleOrderCacheEvent(key, channel);
    }
  }

  private void handleOrderCacheEvent(final String key, final String channel) {
    try {
      final String orderIdStr = key.substring("orders::".length());
      final Long orderId = Long.parseLong(orderIdStr);

      if (channel.contains("expired") || channel.contains("evicted")) {
        final var orderData = ordersService.getOrderById(orderId);
        final var updateEvent = OrderUpdateEventDTO.updated(orderData);
        orderUpdateService.broadcastOrderUpdate(updateEvent);
        logger.info("Broadcasted order update for expired/evicted cache key: {}", key);
      }
    } catch (Exception e) {
      logger.error("Error processing cache event for key: {}", key, e);
    }
  }
}
