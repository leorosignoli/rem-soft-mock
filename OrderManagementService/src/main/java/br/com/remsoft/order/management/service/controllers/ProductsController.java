package br.com.remsoft.order.management.service.controllers;

import br.com.remsoft.order.management.service.services.ProductsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductsController {

  private final ProductsService productsService;

  public ProductsController(ProductsService productsService) {
    this.productsService = productsService;
  }

  @GetMapping("/{productId}")
  public String getProductById(@PathVariable String productId) {
    // This is a placeholder for the actual implementation
    return "List of products";
  }
}
