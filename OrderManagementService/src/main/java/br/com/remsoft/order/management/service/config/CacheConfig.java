package br.com.remsoft.order.management.service.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);

  @Value("${spring.cache.redis.time-to-live}")
  private long cacheTtlMillis;

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    LOGGER.info("Configuring Redis cache manager with improved serialization");
    
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    
    // Fix: Use simpler configuration without problematic type handling
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.findAndRegisterModules();

    // Register PageImplMixin to handle PageImpl serialization/deserialization
    objectMapper.addMixIn(PageImpl.class, PageImplMixin.class);

    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMillis(cacheTtlMillis))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues();

    LOGGER.debug("Cache configuration: TTL={}ms, null values disabled", cacheTtlMillis);

    RedisCacheManager cacheManager =
        RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    LOGGER.info("Redis cache manager configured successfully with improved serialization");
    return cacheManager;
  }
}
