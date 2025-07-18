import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { OrdersService } from '../../services/orders.service';
import { Order, PageRequest, PageResponse } from '../../models/order.model';

@Component({
  selector: 'app-orders-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './orders-list.component.html',
  styleUrls: ['./orders-list.component.css']
})
export class OrdersListComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  loading = false;
  error = '';
  isRealTimeEnabled = false;
  
  pageRequest: PageRequest = {
    page: 0,
    size: 20,
    sortBy: 'orderDate',
    sortDirection: 'desc'
  };

  pageResponse: PageResponse<Order> | null = null;
  private destroy$ = new Subject<void>();

  constructor(private ordersService: OrdersService) {}

  ngOnInit(): void {
    this.loadOrders();
    this.setupRealTimeUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrders(): void {
    this.loading = true;
    this.error = '';

    this.ordersService.getAllOrders(this.pageRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.pageResponse = response;
          this.orders = response.content;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to load orders';
          this.loading = false;
          console.error('Error loading orders:', error);
        }
      });
  }

  onPageChange(page: number): void {
    this.pageRequest.page = page;
    this.loadOrders();
  }

  onSortChange(sortBy: string): void {
    if (this.pageRequest.sortBy === sortBy) {
      this.pageRequest.sortDirection = this.pageRequest.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.pageRequest.sortBy = sortBy;
      this.pageRequest.sortDirection = 'desc';
    }
    this.pageRequest.page = 0;
    this.loadOrders();
  }

  formatCurrency(amount: number): string {
    return this.ordersService.formatCurrency(amount);
  }

  formatDate(dateString: string): string {
    return this.ordersService.formatDate(dateString);
  }

  getTotalItems(order: Order): number {
    return order.orderItems.reduce((total, item) => total + item.quantity, 0);
  }

  getSortIcon(column: string): string {
    if (this.pageRequest.sortBy !== column) {
      return 'sort';
    }
    return this.pageRequest.sortDirection === 'asc' ? 'sort-up' : 'sort-down';
  }

  trackByOrderId(index: number, order: Order): number {
    return order.id;
  }

  setupRealTimeUpdates(): void {
    this.ordersService.subscribeToOrderUpdates()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updateEvent) => {
          this.isRealTimeEnabled = true;
          this.handleOrderUpdate(updateEvent);
        },
        error: (error) => {
          this.isRealTimeEnabled = false;
          console.warn('Real-time updates unavailable:', error);
        }
      });
  }

  handleOrderUpdate(updateEvent: any): void {
    switch (updateEvent.eventType) {
      case 'CREATED':
      case 'UPDATED':
        this.loadOrders();
        break;
      case 'DELETED':
        this.orders = this.orders.filter(order => order.id !== updateEvent.orderId);
        break;
    }
  }

  toggleRealTimeUpdates(): void {
    if (this.isRealTimeEnabled) {
      this.isRealTimeEnabled = false;
    } else {
      this.setupRealTimeUpdates();
    }
  }
}