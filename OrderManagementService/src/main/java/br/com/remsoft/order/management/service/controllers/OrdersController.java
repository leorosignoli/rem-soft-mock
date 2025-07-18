package br.com.remsoft.order.management.service.controllers;

import br.com.remsoft.order.management.service.controllers.dtos.request.CreateOrderRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.request.PageRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import br.com.remsoft.order.management.service.services.OrderUpdateService;
import br.com.remsoft.order.management.service.services.OrdersService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/orders")
public class OrdersController {

  private final OrdersService ordersService;
  private final OrderUpdateService orderUpdateService;

  public OrdersController(OrdersService ordersService, OrderUpdateService orderUpdateService) {
    this.ordersService = ordersService;
    this.orderUpdateService = orderUpdateService;
  }

  @GetMapping("/{orderId}")
  public GetOrderResponseDTO getOrderById(@PathVariable Long orderId) {
    return ordersService.getOrderById(orderId);
  }

  @GetMapping
  public Page<GetOrderResponseDTO> getAllOrders(@ModelAttribute PageRequestDTO pageRequest) {
    return ordersService.getAllOrders(pageRequest.toPageable());
  }

  @GetMapping("/stream")
  public SseEmitter streamOrderUpdates() {
    return orderUpdateService.subscribe();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GetOrderResponseDTO createOrder(
      @Valid @RequestBody CreateOrderRequestDTO createOrderRequestDTO) {
    return ordersService.createOrder(createOrderRequestDTO);
  }
}
