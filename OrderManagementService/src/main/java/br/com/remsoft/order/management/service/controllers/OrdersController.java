package br.com.remsoft.order.management.service.controllers;

import br.com.remsoft.order.management.service.controllers.dtos.request.PageRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import br.com.remsoft.order.management.service.services.OrdersService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrdersController {

  private final OrdersService ordersService;

  public OrdersController(OrdersService ordersService) {
    this.ordersService = ordersService;
  }

  @GetMapping("/{orderId}")
  public GetOrderResponseDTO getOrderById(@PathVariable Long orderId) {
    return ordersService.getOrderById(orderId);
  }

  @GetMapping
  public Page<GetOrderResponseDTO> getAllOrders(@ModelAttribute PageRequestDTO pageRequest) {
    return ordersService.getAllOrders(pageRequest.toPageable());
  }
}
