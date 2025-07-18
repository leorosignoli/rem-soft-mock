package br.com.remsoft.order.management.service.utils;

import br.com.remsoft.order.management.service.repositories.entities.Manufacturer;
import br.com.remsoft.order.management.service.repositories.entities.Order;
import br.com.remsoft.order.management.service.repositories.entities.OrderItem;
import br.com.remsoft.order.management.service.repositories.entities.Product;
import br.com.remsoft.order.management.service.repositories.entities.User;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class TestDataBuilder {

  public static User createUser(Long id, String name, String email) {
    User user = new User();
    user.setId(id);
    user.setName(name);
    user.setEmail(email);
    return user;
  }

  public static User createDefaultUser() {
    return createUser(1L, "John Doe", "john.doe@example.com");
  }

  public static Manufacturer createManufacturer(Long id, String name) {
    Manufacturer manufacturer = new Manufacturer();
    manufacturer.setId(id);
    manufacturer.setName(name);
    return manufacturer;
  }

  public static Manufacturer createDefaultManufacturer() {
    return createManufacturer(1L, "Tech Corp");
  }

  public static Product createProduct(
      Long id, String name, BigDecimal price, Manufacturer manufacturer) {
    Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setPrice(price);
    product.setManufacturer(manufacturer);
    return product;
  }

  public static Product createDefaultProduct() {
    return createProduct(1L, "Test Product", new BigDecimal("99.99"), createDefaultManufacturer());
  }

  public static Order createOrder(
      Long id, OffsetDateTime orderDate, BigDecimal totalAmount, User user) {
    Order order = new Order();
    order.setId(id);
    order.setOrderDate(orderDate);
    order.setTotalAmount(totalAmount);
    order.setUser(user);
    return order;
  }

  public static Order createDefaultOrder() {
    return createOrder(1L, OffsetDateTime.now(), new BigDecimal("199.98"), createDefaultUser());
  }

  public static OrderItem createOrderItem(
      Long id, Integer quantity, BigDecimal unitPrice, Order order, Product product) {
    OrderItem orderItem = new OrderItem();
    orderItem.setId(id);
    orderItem.setQuantity(quantity);
    orderItem.setUnitPrice(unitPrice);
    orderItem.setOrder(order);
    orderItem.setProduct(product);
    return orderItem;
  }

  public static OrderItem createDefaultOrderItem(Order order, Product product) {
    return createOrderItem(1L, 2, new BigDecimal("99.99"), order, product);
  }

  public static Order createCompleteOrder() {
    User user = createDefaultUser();
    Manufacturer manufacturer = createDefaultManufacturer();
    Product product = createDefaultProduct();
    Order order = createOrder(1L, OffsetDateTime.now(), new BigDecimal("199.98"), user);

    OrderItem orderItem = createDefaultOrderItem(order, product);
    order.setOrderItems(Set.of(orderItem));

    return order;
  }
}
