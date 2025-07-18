package br.com.remsoft.order.management.service.services;

import br.com.remsoft.order.management.service.config.CacheWarmingProperties;
import br.com.remsoft.order.management.service.repositories.OrdersRepository;
import br.com.remsoft.order.management.service.repositories.entities.Order;
import br.com.remsoft.order.management.service.services.mappers.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderCacheWarmingService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderCacheWarmingService.class);
    
    private final OrdersRepository ordersRepository;
    private final OrderMapper orderMapper;
    private final CacheManager cacheManager;
    private final CacheWarmingProperties cacheWarmingProperties;
    
    public OrderCacheWarmingService(
        OrdersRepository ordersRepository,
        OrderMapper orderMapper,
        CacheManager cacheManager,
        CacheWarmingProperties cacheWarmingProperties
    ) {
        this.ordersRepository = ordersRepository;
        this.orderMapper = orderMapper;
        this.cacheManager = cacheManager;
        this.cacheWarmingProperties = cacheWarmingProperties;
    }
    
    public void warmAllCaches() {
        if (!cacheWarmingProperties.isEnabled()) {
            logger.info("Cache warming is disabled");
            return;
        }
        
        logger.info("Starting cache warming process...");
        
        warmRecentOrders();
        warmPopularOrders();
        warmPaginatedResults();
        
        logger.info("Cache warming process completed");
    }
    
    public void warmRecentOrders() {
        logger.info("Warming recent orders cache...");
        
        OffsetDateTime since = OffsetDateTime.now().minusDays(cacheWarmingProperties.getRecentDays());
        Pageable pageable = PageRequest.of(0, cacheWarmingProperties.getPageSize(), 
                                         Sort.by(Sort.Direction.DESC, "orderDate"));
        
        Page<Order> recentOrders = ordersRepository.findRecentOrdersWithOrderItems(since, pageable);
        
        recentOrders.getContent().forEach(order -> {
            var orderDto = orderMapper.toGetOrderResponseDTO(order);
            cacheManager.getCache("orders").put(order.getId(), orderDto);
        });
        
        logger.info("Cached {} recent orders", recentOrders.getContent().size());
    }
    
    public void warmPopularOrders() {
        logger.info("Warming popular orders cache...");
        
        warmHighValueOrders();
        warmMultiItemOrders();
        
        logger.info("Popular orders cache warming completed");
    }
    
    private void warmHighValueOrders() {
        Pageable pageable = PageRequest.of(0, cacheWarmingProperties.getPageSize(), 
                                         Sort.by(Sort.Direction.DESC, "totalAmount"));
        
        Page<Order> highValueOrders = ordersRepository.findHighValueOrdersWithOrderItems(
            cacheWarmingProperties.getMinAmount(), pageable);
        
        highValueOrders.getContent().forEach(order -> {
            var orderDto = orderMapper.toGetOrderResponseDTO(order);
            cacheManager.getCache("orders").put(order.getId(), orderDto);
        });
        
        logger.info("Cached {} high-value orders", highValueOrders.getContent().size());
    }
    
    private void warmMultiItemOrders() {
        Pageable pageable = PageRequest.of(0, cacheWarmingProperties.getPageSize(), 
                                         Sort.by(Sort.Direction.DESC, "orderDate"));
        
        Page<Order> multiItemOrders = ordersRepository.findMultiItemOrdersWithOrderItems(pageable);
        
        multiItemOrders.getContent().forEach(order -> {
            var orderDto = orderMapper.toGetOrderResponseDTO(order);
            cacheManager.getCache("orders").put(order.getId(), orderDto);
        });
        
        logger.info("Cached {} multi-item orders", multiItemOrders.getContent().size());
    }
    
    public void warmPaginatedResults() {
        logger.info("Warming paginated results cache...");
        
        List<String> sortFields = List.of("orderDate", "totalAmount");
        List<Sort.Direction> sortDirections = List.of(Sort.Direction.DESC, Sort.Direction.ASC);
        
        for (String sortField : sortFields) {
            for (Sort.Direction direction : sortDirections) {
                warmPagesForSort(sortField, direction);
            }
        }
        
        logger.info("Paginated results cache warming completed");
    }
    
    private void warmPagesForSort(String sortField, Sort.Direction direction) {
        for (int page = 0; page < cacheWarmingProperties.getPageCount(); page++) {
            Pageable pageable = PageRequest.of(page, cacheWarmingProperties.getPageSize(), 
                                             Sort.by(direction, sortField));
            
            Page<Order> orders = ordersRepository.findAllWithOrderItems(pageable);
            
            var pageResponse = orders.map(orderMapper::toGetOrderResponseDTO);
            
            String cacheKey = String.format("%d-%d-%s: %s", 
                page, cacheWarmingProperties.getPageSize(), sortField, direction.name());
            
            cacheManager.getCache("orders-page").put(cacheKey, pageResponse);
        }
        
        logger.info("Cached {} pages for sort: {} {}", 
            cacheWarmingProperties.getPageCount(), sortField, direction.name().toLowerCase());
    }
}