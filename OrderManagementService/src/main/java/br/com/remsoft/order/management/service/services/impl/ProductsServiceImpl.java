package br.com.remsoft.order.management.service.services.impl;

import br.com.remsoft.order.management.service.controllers.dtos.request.AddProductRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.AddProductResponseDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetProductResponseDTO;
import br.com.remsoft.order.management.service.exceptions.NotFoundException;
import br.com.remsoft.order.management.service.repositories.ManufacturersRepository;
import br.com.remsoft.order.management.service.repositories.ProductsRepository;
import br.com.remsoft.order.management.service.services.ProductsService;
import br.com.remsoft.order.management.service.services.mappers.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductsServiceImpl implements ProductsService {

  private final ProductsRepository productsRepository;
  private final ManufacturersRepository manufacturersRepository;
  private final ProductMapper productMapper;

  public ProductsServiceImpl(
      ProductsRepository productsRepository,
      ManufacturersRepository manufacturersRepository,
      final ProductMapper productMapper) {
    this.productsRepository = productsRepository;
    this.manufacturersRepository = manufacturersRepository;
    this.productMapper = productMapper;
  }

  @Override
  public GetProductResponseDTO getProductById(final Long id) {

    return productsRepository
        .findById(id)
        .map(productMapper::toGetProductResponseDTO)
        .orElseThrow(() -> NotFoundException.productNotFound(id));
  }

  @Override
  public AddProductResponseDTO addProduct(final AddProductRequestDTO addProductRequestDTO) {

    final var manufacturer =
        manufacturersRepository
            .findById(addProductRequestDTO.getManufacturerId())
            .orElseThrow(
                () ->
                    NotFoundException.manufacturerNotFound(
                        addProductRequestDTO.getManufacturerId()));

    final var product = productMapper.toProduct(addProductRequestDTO);
    product.setManufacturer(manufacturer);

    final var savedProduct = productsRepository.save(product);

    return productMapper.toAddProductResponseDTO(savedProduct);
  }
}
