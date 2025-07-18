package br.com.remsoft.order.management.service.listeners;

import br.com.remsoft.order.management.service.services.OrderCacheWarmingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmingApplicationListener {

  private static final Logger logger =
      LoggerFactory.getLogger(CacheWarmingApplicationListener.class);

  private final OrderCacheWarmingService orderCacheWarmingService;

  public CacheWarmingApplicationListener(OrderCacheWarmingService orderCacheWarmingService) {
    this.orderCacheWarmingService = orderCacheWarmingService;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    logger.info("Application is ready, starting cache warming...");

    try {
      orderCacheWarmingService.warmAllCaches();
      logger.info("Cache warming completed successfully");
    } catch (Exception e) {
      logger.error("Error during cache warming", e);
    }
  }
}
