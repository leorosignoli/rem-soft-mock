package br.com.remsoft.order.management.service.services.impl;

import br.com.remsoft.order.management.service.controllers.dtos.request.CreateOrderRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.CustomPageDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.OrderUpdateEventDTO;
import br.com.remsoft.order.management.service.exceptions.NotFoundException;
import br.com.remsoft.order.management.service.repositories.OrdersRepository;
import br.com.remsoft.order.management.service.repositories.ProductsRepository;
import br.com.remsoft.order.management.service.repositories.UsersRepository;
import br.com.remsoft.order.management.service.repositories.entities.OrderItem;
import br.com.remsoft.order.management.service.services.OrderUpdateService;
import br.com.remsoft.order.management.service.services.OrdersService;
import br.com.remsoft.order.management.service.services.mappers.OrderMapper;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersServiceImpl implements OrdersService {

  private final OrdersRepository ordersRepository;
  private final UsersRepository usersRepository;
  private final ProductsRepository productsRepository;
  private final OrderMapper orderMapper;
  private final OrderUpdateService orderUpdateService;

  public OrdersServiceImpl(
      OrdersRepository ordersRepository,
      UsersRepository usersRepository,
      ProductsRepository productsRepository,
      OrderMapper orderMapper,
      OrderUpdateService orderUpdateService) {
    this.ordersRepository = ordersRepository;
    this.usersRepository = usersRepository;
    this.productsRepository = productsRepository;
    this.orderMapper = orderMapper;
    this.orderUpdateService = orderUpdateService;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "orders", key = "#id")
  public GetOrderResponseDTO getOrderById(final Long id) {
    return ordersRepository
        .findById(id)
        .map(orderMapper::toGetOrderResponseDTO)
        .orElseThrow(() -> NotFoundException.orderNotFound(id));
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(
      value = "orders-page",
      key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
  public CustomPageDTO<GetOrderResponseDTO> getAllOrders(final Pageable pageable) {
    final var page = ordersRepository.findAll(pageable).map(orderMapper::toGetOrderResponseDTO);
    return new CustomPageDTO<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast());
  }

  @Override
  @Transactional
  @CacheEvict(
      value = {"orders", "orders-page"},
      allEntries = true)
  public GetOrderResponseDTO createOrder(final CreateOrderRequestDTO createOrderRequestDTO) {
    final var user =
        usersRepository
            .findById(createOrderRequestDTO.getUserId())
            .orElseThrow(() -> NotFoundException.userNotFound(createOrderRequestDTO.getUserId()));

    final var order = orderMapper.toOrder(createOrderRequestDTO);
    order.setUser(user);
    order.setOrderDate(OffsetDateTime.now());

    final Set<OrderItem> orderItems = new HashSet<>();
    var totalAmount = BigDecimal.ZERO;

    for (final var orderItemRequest : createOrderRequestDTO.getOrderItems()) {
      final var product =
          productsRepository
              .findById(orderItemRequest.getProductId())
              .orElseThrow(
                  () -> NotFoundException.productNotFound(orderItemRequest.getProductId()));

      final var orderItem = orderMapper.toOrderItem(orderItemRequest);
      orderItem.setProduct(product);
      orderItem.setUnitPrice(product.getPrice());
      orderItem.setOrder(order);

      final var itemTotal =
          product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()));
      totalAmount = totalAmount.add(itemTotal);

      orderItems.add(orderItem);
    }

    order.setOrderItems(orderItems);
    order.setTotalAmount(totalAmount);

    final var savedOrder = ordersRepository.save(order);
    final var response = orderMapper.toGetOrderResponseDTO(savedOrder);

    broadcastOrderCreated(response);

    return response;
  }

  public void broadcastOrderCreated(final GetOrderResponseDTO orderData) {
    final var event = OrderUpdateEventDTO.created(orderData);
    orderUpdateService.broadcastOrderUpdate(event);
  }
}
