import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AppNotification, NotificationType } from '../models/inventory.models';

/**
 * Service for managing global application notifications (toasts).
 * Provides methods for pushing success, error, warning, and info messages.
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationSubject = new Subject<AppNotification>();
  notifications$ = this.notificationSubject.asObservable();

  private counter = 0;

  /**
   * Displays a generic notification.
   */
  show(message: string, type: NotificationType = NotificationType.INFO, title?: string, duration: number = 5000) {
    this.notificationSubject.next({
      id: ++this.counter,
      type,
      message,
      title,
      duration
    });
  }

  /**
   * Shorthand for success notifications.
   */
  success(message: string, title = 'Success') {
    this.show(message, NotificationType.SUCCESS, title);
  }

  /**
   * Shorthand for error notifications.
   */
  error(message: string, title = 'Error') {
    this.show(message, NotificationType.ERROR, title, 8000); // Errors stay longer
  }

  /**
   * Shorthand for warning notifications.
   */
  warn(message: string, title = 'Warning') {
    this.show(message, NotificationType.WARNING, title);
  }

  /**
   * Shorthand for info notifications.
   */
  info(message: string, title = 'Information') {
    this.show(message, NotificationType.INFO, title);
  }
}
