package br.com.remsoft.order.management.service.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public abstract class PageImplMixin<T> extends PageImpl<T> {

  @JsonCreator
  public PageImplMixin(
      @JsonProperty("content") List<T> content,
      @JsonProperty("number") int number,
      @JsonProperty("size") int size,
      @JsonProperty("totalElements") long totalElements,
      @JsonProperty("pageable") JsonNode pageable,
      @JsonProperty("last") boolean last,
      @JsonProperty("totalPages") int totalPages,
      @JsonProperty("sort") JsonNode sort,
      @JsonProperty("first") boolean first,
      @JsonProperty("numberOfElements") int numberOfElements,
      @JsonProperty("empty") boolean empty) {
    super(content, PageRequest.of(number, size), totalElements);
  }
}
