import { Component, OnInit, OnDestroy, ChangeDetectorRef, NgZone, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { OrdersService } from '../../services/orders.service';
import { Order, PageRequest, PageResponse } from '../../models/order.model';

@Component({
  selector: 'app-orders-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders-list.component.html',
  styleUrls: ['./orders-list.component.css']
})
export class OrdersListComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  loading = false;
  error = '';
  isRealTimeEnabled = false;
  
  // Signal to control animation for newly inserted orders
  newOrderIds = signal<Set<number>>(new Set());
  
  pageRequest: PageRequest = {
    page: 0,
    size: 20,
    sortBy: 'orderDate',
    sortDirection: 'desc'
  };

  pageResponse: PageResponse<Order> | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private ordersService: OrdersService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.setupRealTimeUpdates();
    this.cdr.markForCheck();
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
          // Clear animation state when loading fresh data
          this.newOrderIds.set(new Set());
          this.cdr.markForCheck();
        },
        error: (error) => {
          this.error = 'Falha ao carregar pedidos';
          this.loading = false;
          this.cdr.markForCheck();
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

  isNewOrder(orderId: number): boolean {
    return this.newOrderIds().has(orderId);
  }

  navigateToOrder(orderId: number): void {
    this.router.navigate(['/orders', orderId]);
  }

  setupRealTimeUpdates(): void {
    this.ordersService.subscribeToOrderUpdates()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updateEvent) => {
          // Run in Angular zone to ensure change detection triggers
          this.ngZone.run(() => {
            this.isRealTimeEnabled = true;
            this.handleOrderUpdate(updateEvent);
            this.cdr.markForCheck();
          });
        },
        error: (error) => {
          this.ngZone.run(() => {
            this.isRealTimeEnabled = false;
            this.cdr.markForCheck();
          });
          console.warn('Atualizações em tempo real indisponíveis:', error);
        }
      });
  }

  handleOrderUpdate(updateEvent: any): void {
    console.log('Processing order update event:', updateEvent);
    
    switch (updateEvent.eventType) {
      case 'CREATED':
        this.handleOrderCreated(updateEvent.orderId);
        break;
      case 'UPDATED':
        this.handleOrderUpdated(updateEvent.orderId);
        break;
      case 'DELETED':
        this.handleOrderDeleted(updateEvent.orderId);
        break;
      default:
        console.warn('Unknown event type:', updateEvent.eventType);
        // Fallback: reload orders for any unknown event type
        this.loadOrders();
        break;
    }
  }

  private handleOrderCreated(orderId: number): void {
    // Check if we're on the first page and sorted by date desc to show new orders
    if (this.pageRequest.page === 0 && 
        this.pageRequest.sortBy === 'orderDate' && 
        this.pageRequest.sortDirection === 'desc') {
      
      // Fetch the new order and add it to the list
      this.ordersService.getOrderById(orderId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (newOrder) => {
            // Check if order already exists to avoid duplicates
            if (!this.orders.find(order => order.id === newOrder.id)) {
              // Add to the beginning of the array
              this.orders.unshift(newOrder);
              
              // Mark as new for animation
              const currentNewIds = this.newOrderIds();
              currentNewIds.add(newOrder.id);
              this.newOrderIds.set(new Set(currentNewIds));
              
              // Remove the animation class after animation completes
              setTimeout(() => {
                const updatedIds = this.newOrderIds();
                updatedIds.delete(newOrder.id);
                this.newOrderIds.set(new Set(updatedIds));
                this.cdr.markForCheck();
              }, 1000); // Match the animation duration
              
              // Update pagination info
              if (this.pageResponse) {
                this.pageResponse.totalElements++;
                // Remove the last item if we exceed page size
                if (this.orders.length > this.pageRequest.size) {
                  this.orders.pop();
                }
              }
              
              this.cdr.markForCheck();
            }
          },
          error: (error) => {
            console.error('Error fetching new order:', error);
            // Fallback to reloading if individual fetch fails
            this.loadOrders();
          }
        });
    } else {
      // If not on first page or different sorting, just update pagination count
      if (this.pageResponse) {
        this.pageResponse.totalElements++;
      }
      this.cdr.markForCheck();
    }
  }

  private handleOrderUpdated(orderId: number): void {
    // Find and update the existing order
    const existingOrderIndex = this.orders.findIndex(order => order.id === orderId);
    if (existingOrderIndex !== -1) {
      // Fetch the updated order
      this.ordersService.getOrderById(orderId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (updatedOrder) => {
            this.orders[existingOrderIndex] = updatedOrder;
            this.cdr.markForCheck();
          },
          error: (error) => {
            console.error('Error fetching updated order:', error);
            // Fallback to reloading if individual fetch fails
            this.loadOrders();
          }
        });
    } else {
      // Order not in current page, just trigger change detection for any UI updates
      this.cdr.markForCheck();
    }
  }

  private handleOrderDeleted(orderId: number): void {
    // Remove from current list if present
    const initialLength = this.orders.length;
    this.orders = this.orders.filter(order => order.id !== orderId);
    
    if (this.orders.length < initialLength && this.pageResponse) {
      this.pageResponse.totalElements--;
      this.pageResponse.content = this.orders;
    }
    
    this.cdr.markForCheck();
  }

  toggleRealTimeUpdates(): void {
    if (this.isRealTimeEnabled) {
      this.isRealTimeEnabled = false;
    } else {
      this.setupRealTimeUpdates();
    }
  }
}