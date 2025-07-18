package br.com.remsoft.order.management.service.controllers;

import br.com.remsoft.order.management.service.repositories.OrdersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrdersControllerCacheComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private OrdersRepository ordersRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void getAllOrders_whenCalledMultipleTimes_shouldUseCache() throws Exception {
        // First call - should hit the database
        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ordersRepository, times(1)).findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
        assertThat(cacheManager.getCache("orders-page")).isNotNull();

        // Second call - should be served from cache
        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ordersRepository, times(1)).findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
    }
}
