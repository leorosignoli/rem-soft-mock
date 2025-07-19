import { Component, Input, Output, EventEmitter, signal, effect, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Notification } from '../../services/notification.service';

@Component({
  selector: 'app-notification-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-popup.component.html',
  styleUrls: ['./notification-popup.component.css']
})
export class NotificationPopupComponent implements OnInit {
  @Input() notification!: Notification;
  @Input() index: number = 0;
  @Output() dismiss = new EventEmitter<string>();
  
  visible = signal(false);

  constructor() {
    effect(() => {
      if (this.notification) {
        console.log('NotificationPopupComponent: Notification received', this.notification);
        this.visible.set(false);
        setTimeout(() => {
          this.visible.set(true);
        }, 50);
      }
    });
  }

  ngOnInit(): void {
    console.log('NotificationPopupComponent: Component initialized with notification', this.notification);
  }

  onDismiss() {
    this.visible.set(false);
    setTimeout(() => {
      this.dismiss.emit(this.notification.id);
    }, 300);
  }

  getTopPosition(): string {
    return `${80 + (this.index * 80)}px`;
  }

  getNotificationClass(): string {
    return `notification-${this.notification.type}`;
  }
}