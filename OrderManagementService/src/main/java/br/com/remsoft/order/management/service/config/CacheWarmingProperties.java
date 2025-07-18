package br.com.remsoft.order.management.service.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cache.warming")
public class CacheWarmingProperties {

  private boolean enabled = true;
  private int recentDays = 30;
  private BigDecimal minAmount = BigDecimal.valueOf(100.00);
  private int pageCount = 5;
  private int pageSize = 20;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getRecentDays() {
    return recentDays;
  }

  public void setRecentDays(int recentDays) {
    this.recentDays = recentDays;
  }

  public BigDecimal getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(BigDecimal minAmount) {
    this.minAmount = minAmount;
  }

  public int getPageCount() {
    return pageCount;
  }

  public void setPageCount(int pageCount) {
    this.pageCount = pageCount;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
