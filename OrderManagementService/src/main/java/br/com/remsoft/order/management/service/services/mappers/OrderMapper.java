package br.com.remsoft.order.management.service.services.mappers;

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
}
