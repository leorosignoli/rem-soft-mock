package br.com.remsoft.order.management.service.services.mappers;

import br.com.remsoft.order.management.service.controllers.dtos.request.AddProductRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.AddProductResponseDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetProductResponseDTO;
import br.com.remsoft.order.management.service.repositories.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "manufacturerName", source = "product.manufacturer.name")
  @Mapping(target = "manufacturerId", source = "product.manufacturer.id")
  GetProductResponseDTO toGetProductResponseDTO(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "manufacturer", ignore = true)
  Product toProduct(AddProductRequestDTO addProductRequestDTO);

  @Mapping(target = "manufacturerName", source = "product.manufacturer.name")
  @Mapping(target = "manufacturerId", source = "product.manufacturer.id")
  AddProductResponseDTO toAddProductResponseDTO(Product product);
}
