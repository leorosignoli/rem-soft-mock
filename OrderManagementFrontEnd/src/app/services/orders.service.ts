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

      const eventSource = new EventSource(`${this.apiUrl}/stream`);
      
      eventSource.onmessage = event => {
        const data = JSON.parse(event.data);
        observer.next(data);
      };
      
      eventSource.onerror = error => {
        observer.error(error);
      };
      
      return () => eventSource.close();
    });
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}