package br.com.remsoft.order.management.service.services.impl;

import br.com.remsoft.order.management.service.controllers.dtos.response.OrderUpdateEventDTO;
import br.com.remsoft.order.management.service.services.OrderUpdateService;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class OrderUpdateServiceImpl implements OrderUpdateService {

  private static final Logger logger = LoggerFactory.getLogger(OrderUpdateServiceImpl.class);
  private static final long SSE_TIMEOUT = 30 * 60 * 1000L;
  private static final long HEARTBEAT_INTERVAL = 30;

  private final ConcurrentMap<SseEmitter, Long> emitters = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public OrderUpdateServiceImpl() {
    startHeartbeat();
  }

  @Override
  public SseEmitter subscribe() {
    final SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
    emitters.put(emitter, System.currentTimeMillis());

    emitter.onCompletion(() -> removeEmitter(emitter));
    emitter.onTimeout(() -> removeEmitter(emitter));
    emitter.onError(throwable -> removeEmitter(emitter));

    try {
      emitter.send(SseEmitter.event().name("connected").data("Connected to order updates stream"));
    } catch (IOException e) {
      logger.error("Error sending initial connection message", e);
      removeEmitter(emitter);
    }

    logger.info("New SSE connection established. Total connections: {}", emitters.size());
    return emitter;
  }

  @Override
  public void broadcastOrderUpdate(final OrderUpdateEventDTO event) {
    if (emitters.isEmpty()) {
      return;
    }

    emitters.keySet().parallelStream()
        .forEach(
            emitter -> {
              try {
                emitter.send(SseEmitter.event().name("order-update").data(event));
              } catch (IOException e) {
                logger.error("Error sending order update event", e);
                removeEmitter(emitter);
              }
            });

    logger.info(
        "Broadcasted order update event: {} to {} connections", event.eventType(), emitters.size());
  }

  @Override
  public void removeEmitter(final SseEmitter emitter) {
    emitters.remove(emitter);
    try {
      emitter.complete();
    } catch (Exception e) {
      logger.debug("Error completing emitter", e);
    }
    logger.info("SSE connection removed. Total connections: {}", emitters.size());
  }

  private void startHeartbeat() {
    scheduler.scheduleWithFixedDelay(
        this::sendHeartbeat, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
  }

  private void sendHeartbeat() {
    if (emitters.isEmpty()) {
      return;
    }

    emitters.keySet().parallelStream()
        .forEach(
            emitter -> {
              try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
              } catch (IOException e) {
                logger.debug("Heartbeat failed, removing emitter", e);
                removeEmitter(emitter);
              }
            });
  }
}
