package br.com.remsoft.order.management.service.exceptions;

public class NotFoundException extends RuntimeException {
  private NotFoundException(String message) {
    super(message);
  }

  public static NotFoundException ProductNotFound(Long id) {
    return new NotFoundException("Product with id " + id + " not found.");
  }
}
