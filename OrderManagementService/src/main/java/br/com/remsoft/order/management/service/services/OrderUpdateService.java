package br.com.remsoft.order.management.service.services;

import br.com.remsoft.order.management.service.controllers.dtos.response.OrderUpdateEventDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface OrderUpdateService {

  SseEmitter subscribe();

  void broadcastOrderUpdate(OrderUpdateEventDTO event);

  void removeEmitter(SseEmitter emitter);
}
