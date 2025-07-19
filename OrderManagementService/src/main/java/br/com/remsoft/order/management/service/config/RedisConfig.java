package br.com.remsoft.order.management.service.config;

import static br.com.remsoft.order.management.service.config.CacheConfig.getGenericJackson2JsonRedisSerializer;

import br.com.remsoft.order.management.service.listeners.OrderCacheEvictionListener;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${redis.keyspace-notifications.enabled:true}")
  private boolean keyspaceNotificationsEnabled;

  @Value("${redis.keyspace-notifications.config:Ex}")
  private String keyspaceNotificationsConfig;

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    final RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    GenericJackson2JsonRedisSerializer serializer = getGenericJackson2JsonRedisSerializer();

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      OrderCacheEvictionListener orderCacheEvictionListener) {
    final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);

    if (keyspaceNotificationsEnabled) {
      configureKeyspaceNotifications(connectionFactory);
      container.addMessageListener(
          orderCacheEvictionListener, new PatternTopic("__keyevent@0__:expired"));
      container.addMessageListener(
          orderCacheEvictionListener, new PatternTopic("__keyevent@0__:evicted"));
    }

    return container;
  }

  private void configureKeyspaceNotifications(final RedisConnectionFactory connectionFactory) {
    try {
      connectionFactory
          .getConnection()
          .serverCommands()
          .setConfig("notify-keyspace-events", keyspaceNotificationsConfig);
    } catch (Exception e) {
      throw new BeanInitializationException("Failed to configure Redis keyspace notifications", e);
    }
  }
}
