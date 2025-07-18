import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Order, PageRequest, PageResponse, OrderUpdateEvent } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private readonly apiUrl = 'http://localhost:8080/orders';

  constructor(private http: HttpClient) {}

  getAllOrders(pageRequest: PageRequest): Observable<PageResponse<Order>> {
    let params = new HttpParams()
      .set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortBy', pageRequest.sortBy)
      .set('sortDirection', pageRequest.sortDirection);

    return this.http.get<PageResponse<Order>>(this.apiUrl, { params });
  }

  getOrderById(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`);
  }

  subscribeToOrderUpdates(): Observable<OrderUpdateEvent> {
    return new Observable(observer => {
      // Check if we're in a browser environment
      if (typeof EventSource === 'undefined') {
        observer.error(new Error('EventSource not available'));
        return;
      }

      const sseUrl = `${this.apiUrl}/stream`;
      console.log('Connecting to SSE endpoint:', sseUrl);
      
      const eventSource = new EventSource(sseUrl);
      
      eventSource.onopen = () => {
        console.log('SSE connection opened successfully');
      };
      
      // Listen to the specific 'order-update' event
      eventSource.addEventListener('order-update', (event: MessageEvent) => {
        try {
          console.log('Received order-update event:', event.data);
          const backendEvent = JSON.parse(event.data);
          // Transform backend event structure to frontend structure
          const frontendEvent: OrderUpdateEvent = {
            orderId: backendEvent.orderId,
            eventType: this.mapBackendEventType(backendEvent.eventType),
            timestamp: backendEvent.timestamp
          };
          console.log('Transformed event:', frontendEvent);
          observer.next(frontendEvent);
        } catch (error) {
          console.error('Error parsing SSE event:', error);
        }
      });

      // Listen to connection events
      eventSource.addEventListener('connected', (event: MessageEvent) => {
        console.log('SSE Connected:', event.data);
      });

      // Listen to heartbeat events
      eventSource.addEventListener('heartbeat', (event: MessageEvent) => {
        console.debug('SSE Heartbeat:', event.data);
      });
      
      eventSource.onerror = error => {
        console.error('SSE Error:', error);
        console.error('SSE ReadyState:', eventSource.readyState);
        observer.error(error);
      };
      
      return () => {
        console.log('Closing SSE connection');
        eventSource.close();
      };
    });
  }

  private mapBackendEventType(backendEventType: string): 'CREATED' | 'UPDATED' | 'DELETED' {
    // Backend currently only sends ORDER_UPDATED, map it to UPDATED
    switch (backendEventType) {
      case 'ORDER_UPDATED':
        return 'UPDATED';
      case 'ORDER_CREATED':
        return 'CREATED';
      case 'ORDER_DELETED':
        return 'DELETED';
      default:
        return 'UPDATED'; // Default fallback
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}