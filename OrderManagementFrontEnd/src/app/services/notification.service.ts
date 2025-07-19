import { Injectable, signal } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Notification {
  id: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  autoClose: boolean;
  duration: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = signal<Notification[]>([]);
  private notificationSubject = new BehaviorSubject<Notification[]>([]);
  
  notifications$ = this.notificationSubject.asObservable();

  constructor() {
    this.notificationSubject.next(this.notifications());
  }

  show(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'success', autoClose: boolean = true, duration: number = 5000): string {
    const id = this.generateId();
    const notification: Notification = {
      id,
      message,
      type,
      autoClose,
      duration
    };

    console.log('NotificationService: Adding notification', notification);
    
    const currentNotifications = this.notifications();
    const updatedNotifications = [...currentNotifications, notification];
    this.notifications.set(updatedNotifications);
    this.notificationSubject.next(updatedNotifications);

    console.log('NotificationService: Updated notifications list', updatedNotifications);

    if (autoClose) {
      setTimeout(() => {
        this.dismiss(id);
      }, duration);
    }

    return id;
  }

  dismiss(id: string): void {
    const currentNotifications = this.notifications();
    const updatedNotifications = currentNotifications.filter(n => n.id !== id);
    this.notifications.set(updatedNotifications);
    this.notificationSubject.next(updatedNotifications);
  }

  dismissAll(): void {
    this.notifications.set([]);
    this.notificationSubject.next([]);
  }

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }
}