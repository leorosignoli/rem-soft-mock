package br.com.remsoft.order.management.service.services.mappers;

import br.com.remsoft.order.management.service.controllers.dtos.response.GetProductResponseDTO;
import br.com.remsoft.order.management.service.repositories.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {

  @Mapping(target = "manufacturerName", source = "product.manufacturer.name")
  @Mapping(target = "manufacturerId", source = "product.manufacturer.id")
  GetProductResponseDTO toGetProductResponseDTO(Product product);
}
