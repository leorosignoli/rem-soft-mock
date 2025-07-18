package br.com.remsoft.order.management.service.exceptions;

public class NotFoundException extends RuntimeException {
  private NotFoundException(String message) {
    super(message);
  }

  public static NotFoundException productNotFound(Long id) {
    return new NotFoundException("Product with id " + id + " not found.");
  }

  public static NotFoundException orderNotFound(Long id) {
    return new NotFoundException("Order with id " + id + " not found.");
  }

  public static NotFoundException manufacturerNotFound(Long id) {
    return new NotFoundException("Manufacturer with id " + id + " not found.");
  }

  public static NotFoundException userNotFound(Long id) {
    return new NotFoundException("User with id " + id + " not found.");
  }
}
