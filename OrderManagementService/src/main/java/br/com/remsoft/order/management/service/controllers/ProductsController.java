package br.com.remsoft.order.management.service.controllers;

import br.com.remsoft.order.management.service.controllers.dtos.request.AddProductRequestDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.AddProductResponseDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetProductResponseDTO;
import br.com.remsoft.order.management.service.services.ProductsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductsController {

  private final ProductsService productsService;

  public ProductsController(ProductsService productsService) {
    this.productsService = productsService;
  }

  @GetMapping("/{productId}")
  public GetProductResponseDTO getProductById(@PathVariable Long productId) {
    return productsService.getProductById(productId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AddProductResponseDTO addProduct(
      @Valid @RequestBody AddProductRequestDTO addProductRequestDTO) {
    return productsService.addProduct(addProductRequestDTO);
  }
}
