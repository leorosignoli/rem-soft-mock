package br.com.remsoft.order.management.service.services;

import br.com.remsoft.order.management.service.controllers.dtos.request.AddProductRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.AddProductResponseDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetProductResponseDTO;

public interface ProductsService {

  GetProductResponseDTO getProductById(Long id);

  AddProductResponseDTO addProduct(AddProductRequestDTO addProductRequestDTO);
}
