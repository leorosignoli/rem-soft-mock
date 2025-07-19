import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { OrdersService } from '../../services/orders.service';
import { NotificationService } from '../../services/notification.service';
import { Order } from '../../models/order.model';
import { NotificationContainerComponent } from '../notification-container/notification-container.component';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule, NotificationContainerComponent],
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit, OnDestroy {
  order: Order | null = null;
  loading = false;
  error = '';
  
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ordersService: OrdersService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const orderId = params.get('id');
        if (orderId) {
          this.loadOrder(+orderId);
        }
      });
    
    this.setupSSENotifications();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrder(orderId: number): void {
    this.loading = true;
    this.error = '';

    this.ordersService.getOrderById(orderId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (order) => {
          this.order = order;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.error = 'Falha ao carregar detalhes do pedido';
          this.loading = false;
          this.cdr.detectChanges();
          console.error('Error loading order:', error);
        }
      });
  }

  formatCurrency(amount: number): string {
    return this.ordersService.formatCurrency(amount);
  }

  formatDate(dateString: string): string {
    return this.ordersService.formatDate(dateString);
  }

  getTotalItems(): number {
    return this.order?.orderItems.reduce((total, item) => total + item.quantity, 0) || 0;
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }

  trackByItemId(index: number, item: any): number {
    return item.id;
  }

  private setupSSENotifications(): void {
    this.ordersService.subscribeToOrderUpdates()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (event) => {
          console.log('Received SSE event in order details:', event);
          if (event.eventType === 'CREATED') {
            this.showNewOrderNotification(event.orderId);
          }
        },
        error: (error) => {
          console.error('SSE connection error:', error);
        }
      });
  }

  private showNewOrderNotification(orderId: number): void {
    const message = `Novo pedido <a href="/orders/${orderId}">#${orderId}</a> foi criado!`;
    console.log('OrderDetailsComponent: Showing notification for order', orderId);
    this.notificationService.show(message, 'success');
  }
}