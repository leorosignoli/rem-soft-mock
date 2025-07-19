import { Component, Input, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notification-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-popup.component.html',
  styleUrls: ['./notification-popup.component.css']
})
export class NotificationPopupComponent {
  @Input() message: string = '';
  
  show = signal(false);
  visible = signal(false);

  constructor() {
    effect(() => {
      if (this.show()) {
        this.visible.set(true);
        setTimeout(() => {
          this.visible.set(false);
          setTimeout(() => {
            this.show.set(false);
          }, 300);
        }, 5000);
      }
    });
  }

  triggerNotification() {
    this.show.set(false);
    setTimeout(() => {
      this.show.set(true);
    }, 10);
  }
}