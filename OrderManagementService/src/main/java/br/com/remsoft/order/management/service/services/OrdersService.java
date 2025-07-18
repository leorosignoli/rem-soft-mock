package br.com.remsoft.order.management.service.services;

import br.com.remsoft.order.management.service.controllers.dtos.request.CreateOrderRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.CustomPageDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import org.springframework.data.domain.Pageable;

public interface OrdersService {

  GetOrderResponseDTO getOrderById(Long id);

  CustomPageDTO<GetOrderResponseDTO> getAllOrders(Pageable pageable);

  GetOrderResponseDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO);
}
