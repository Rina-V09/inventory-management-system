import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppNotification, NotificationType } from '../models/inventory.models';
import { NotificationService } from '../services/notification.service';
import { Subscription } from 'rxjs';

/**
 * A global, standalone component for rendering premium application notifications.
 * Features glassmorphism design, auto-dismiss, and smooth animations.
 */
@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-container">
      @for (n of activeNotifications; track n.id) {
        <div class="notification-toast" [ngClass]="getToastClass(n.type)">
          <div class="toast-icon">
            <span class="material-icons">{{ getIcon(n.type) }}</span>
          </div>
          <div class="toast-content">
            <div class="toast-title">{{ n.title }}</div>
            <div class="toast-message">{{ n.message }}</div>
          </div>
          <button class="toast-close" (click)="removeNotification(n.id)">
            <span class="material-icons">close</span>
          </button>
          <div class="toast-progress" [style.animationDuration.ms]="n.duration"></div>
        </div>
      }
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      bottom: 24px;
      right: 24px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-width: 400px;
    }

    .notification-toast {
      display: flex;
      align-items: flex-start;
      padding: 16px;
      border-radius: 12px;
      background: rgba(255, 255, 255, 0.85);
      backdrop-filter: blur(10px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
      border: 1px solid rgba(255, 255, 255, 0.3);
      position: relative;
      overflow: hidden;
      animation: slideIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
      pointer-events: auto;
    }

    @keyframes slideIn {
      from { transform: translateX(120%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }

    .toast-icon {
      margin-right: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .toast-icon .material-icons { font-size: 24px; }

    .toast-content { flex: 1; }

    .toast-title {
      font-weight: 700;
      font-size: 0.95rem;
      margin-bottom: 2px;
      color: #1e293b;
    }

    .toast-message {
      font-size: 0.85rem;
      color: #64748b;
      line-height: 1.4;
    }

    .toast-close {
      background: none;
      border: none;
      cursor: pointer;
      color: #94a3b8;
      padding: 4px;
      margin-top: -4px;
      margin-right: -4px;
      display: flex;
      transition: color 0.2s;
    }

    .toast-close:hover { color: #1e293b; }

    .toast-progress {
      position: absolute;
      bottom: 0;
      left: 0;
      height: 4px;
      background: rgba(0, 0, 0, 0.1);
      width: 100%;
      transform-origin: left;
      animation: progress linear forwards;
    }

    @keyframes progress {
      from { transform: scaleX(1); }
      to { transform: scaleX(0); }
    }

    /* Status Colors */
    .toast-success { border-left: 6px solid #10b981; }
    .toast-success .toast-icon { color: #10b981; }
    
    .toast-error { border-left: 6px solid #ef4444; }
    .toast-error .toast-icon { color: #ef4444; }
    
    .toast-warning { border-left: 6px solid #f59e0b; }
    .toast-warning .toast-icon { color: #f59e0b; }
    
    .toast-info { border-left: 6px solid #3b82f6; }
    .toast-info .toast-icon { color: #3b82f6; }
  `]
})
export class NotificationComponent implements OnInit, OnDestroy {
  private readonly notificationService = inject(NotificationService);
  private subscription!: Subscription;

  activeNotifications: AppNotification[] = [];

  ngOnInit() {
    this.subscription = this.notificationService.notifications$.subscribe(n => {
      this.activeNotifications.push(n);
      
      // Auto-remove after duration
      if (n.duration && n.duration > 0) {
        setTimeout(() => this.removeNotification(n.id), n.duration);
      }
    });
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  removeNotification(id: number) {
    this.activeNotifications = this.activeNotifications.filter(n => n.id !== id);
  }

  getToastClass(type: NotificationType): string {
    switch (type) {
      case NotificationType.SUCCESS: return 'toast-success';
      case NotificationType.ERROR: return 'toast-error';
      case NotificationType.WARNING: return 'toast-warning';
      default: return 'toast-info';
    }
  }

  getIcon(type: NotificationType): string {
    switch (type) {
      case NotificationType.SUCCESS: return 'check_circle';
      case NotificationType.ERROR: return 'error';
      case NotificationType.WARNING: return 'warning';
      default: return 'info';
    }
  }
}
