package br.com.remsoft.order.management.service.services.mappers;

import br.com.remsoft.order.management.service.controllers.dtos.request.CreateOrderItemRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.request.CreateOrderRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import br.com.remsoft.order.management.service.repositories.entities.Order;
import br.com.remsoft.order.management.service.repositories.entities.OrderItem;
import br.com.remsoft.order.management.service.repositories.entities.Product;
import br.com.remsoft.order.management.service.repositories.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  GetOrderResponseDTO toGetOrderResponseDTO(Order order);

  GetOrderResponseDTO.UserDTO toUserDTO(User user);

  GetOrderResponseDTO.OrderItemDTO toOrderItemDTO(OrderItem orderItem);

  @Mapping(target = "manufacturerName", source = "product.manufacturer.name")
  GetOrderResponseDTO.ProductDTO toProductDTO(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "orderDate", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "orderItems", ignore = true)
  Order toOrder(CreateOrderRequestDTO createOrderRequestDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "unitPrice", ignore = true)
  @Mapping(target = "order", ignore = true)
  OrderItem toOrderItem(CreateOrderItemRequestDTO createOrderItemRequestDTO);
}
