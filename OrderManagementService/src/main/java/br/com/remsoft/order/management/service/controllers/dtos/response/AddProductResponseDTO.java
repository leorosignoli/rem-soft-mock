package br.com.remsoft.order.management.service.controllers.dtos.response;

import java.math.BigDecimal;

public class AddProductResponseDTO {
  private Long id;
  private String name;
  private BigDecimal price;
  private String manufacturerName;
  private Long manufacturerId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getManufacturerName() {
    return manufacturerName;
  }

  public void setManufacturerName(String manufacturerName) {
    this.manufacturerName = manufacturerName;
  }

  public Long getManufacturerId() {
    return manufacturerId;
  }

  public void setManufacturerId(Long manufacturerId) {
    this.manufacturerId = manufacturerId;
  }
}
