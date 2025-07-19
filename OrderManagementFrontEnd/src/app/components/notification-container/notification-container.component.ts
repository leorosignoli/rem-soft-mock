import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { NotificationService, Notification } from '../../services/notification.service';
import { NotificationPopupComponent } from '../notification-popup/notification-popup.component';

@Component({
  selector: 'app-notification-container',
  standalone: true,
  imports: [CommonModule, NotificationPopupComponent],
  template: `
    <app-notification-popup
      *ngFor="let notification of notifications; let i = index; trackBy: trackByNotificationId"
      [notification]="notification"
      [index]="i"
      (dismiss)="onDismiss($event)">
    </app-notification-popup>
  `
})
export class NotificationContainerComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('NotificationContainerComponent: Initializing...');
    this.notificationService.notifications$
      .pipe(takeUntil(this.destroy$))
      .subscribe(notifications => {
        console.log('NotificationContainerComponent: Received notifications', notifications);
        this.notifications = notifications;
        this.cdr.detectChanges();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onDismiss(notificationId: string): void {
    this.notificationService.dismiss(notificationId);
  }

  trackByNotificationId(index: number, notification: Notification): string {
    return notification.id;
  }
}