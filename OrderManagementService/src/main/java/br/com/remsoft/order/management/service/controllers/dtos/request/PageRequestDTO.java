package br.com.remsoft.order.management.service.controllers.dtos.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {
  private int page = 0;
  private int size = 20;
  private String sortBy = "orderDate";
  private String sortDirection = "desc";

  public PageRequestDTO() {}

  public PageRequestDTO(int page, int size, String sortBy, String sortDirection) {
    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
  }

  public Pageable toPageable() {
    Sort sort =
        sortDirection.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    return PageRequest.of(page, size, sort);
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
  }
}
