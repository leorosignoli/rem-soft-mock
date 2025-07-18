package br.com.remsoft.order.management.service.services.impl;

import br.com.remsoft.order.management.service.controllers.dtos.response.GetProductResponseDTO;
import br.com.remsoft.order.management.service.exceptions.NotFoundException;
import br.com.remsoft.order.management.service.repositories.ProductsRepository;
import br.com.remsoft.order.management.service.services.ProductsService;
import br.com.remsoft.order.management.service.services.mappers.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductsServiceImpl implements ProductsService {

  private final ProductsRepository productsRepository;
  private final ProductMapper productMapper;

  public ProductsServiceImpl(
      ProductsRepository productsRepository, final ProductMapper productMapper) {
    this.productsRepository = productsRepository;
    this.productMapper = productMapper;
  }

  @Override
  public GetProductResponseDTO getProductById(final Long id) {

    return productsRepository
        .findById(id)
        .map(productMapper::toGetProductResponseDTO)
        .orElseThrow(() -> NotFoundException.productNotFound(id));
  }
}
