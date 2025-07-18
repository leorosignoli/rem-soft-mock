package br.com.remsoft.order.management.service.controllers.dtos.response;

import java.math.BigDecimal;

public record GetProductResponseDTO(
    Long id, String name, BigDecimal price, String manufacturerName, String manufacturerId) {}
