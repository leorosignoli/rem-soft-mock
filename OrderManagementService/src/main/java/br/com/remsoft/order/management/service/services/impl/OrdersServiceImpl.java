package br.com.remsoft.order.management.service.services.impl;

import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import br.com.remsoft.order.management.service.exceptions.NotFoundException;
import br.com.remsoft.order.management.service.repositories.OrdersRepository;
import br.com.remsoft.order.management.service.services.OrdersService;
import br.com.remsoft.order.management.service.services.mappers.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService {

  private final OrdersRepository ordersRepository;
  private final OrderMapper orderMapper;

  public OrdersServiceImpl(OrdersRepository ordersRepository, OrderMapper orderMapper) {
    this.ordersRepository = ordersRepository;
    this.orderMapper = orderMapper;
  }

  @Override
  public GetOrderResponseDTO getOrderById(final Long id) {
    return ordersRepository
        .findById(id)
        .map(orderMapper::toGetOrderResponseDTO)
        .orElseThrow(() -> NotFoundException.orderNotFound(id));
  }

  @Override
  public Page<GetOrderResponseDTO> getAllOrders(final Pageable pageable) {
    return ordersRepository.findAll(pageable).map(orderMapper::toGetOrderResponseDTO);
  }
}