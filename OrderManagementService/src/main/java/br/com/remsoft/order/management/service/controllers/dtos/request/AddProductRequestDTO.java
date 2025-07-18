package br.com.remsoft.order.management.service.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class AddProductRequestDTO {
  @NotBlank(message = "Product name cannot be blank")
  private String name;

  @NotNull(message = "Price cannot be null")
  @Positive(message = "Price must be positive")
  private BigDecimal price;

  @NotNull(message = "Manufacturer ID cannot be null")
  @Positive(message = "Manufacturer ID must be positive")
  private Long manufacturerId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Long getManufacturerId() {
    return manufacturerId;
  }

  public void setManufacturerId(Long manufacturerId) {
    this.manufacturerId = manufacturerId;
  }
}
