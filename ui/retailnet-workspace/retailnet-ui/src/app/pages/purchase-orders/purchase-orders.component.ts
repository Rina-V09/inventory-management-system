import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PurchaseOrderService } from '../../shared/services/purchase-order.service';
import { PurchaseOrderDTO } from '../../shared/models/inventory.models';
import { FilterStatusPipe } from '../../shared/pipes/filter-status.pipe';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-purchase-orders',
  standalone: true,
  imports: [CommonModule, FilterStatusPipe],
  templateUrl: './purchase-orders.component.html',
  styleUrls: ['./purchase-orders.component.css']
})
export class PurchaseOrdersComponent implements OnInit {
  private readonly poService = inject(PurchaseOrderService);
  private readonly route = inject(ActivatedRoute);
  
  orders: PurchaseOrderDTO[] = [];
  displayOrders: PurchaseOrderDTO[] = [];
  isLoading = true;
  isTrackingView = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.isTrackingView = params['view'] === 'tracking';
      this.loadOrders();
    });
  }

  loadOrders(): void {
    this.isLoading = true;
    this.poService.getAllOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load purchase orders', err);
        // Mock data for demo if API fails
        this.orders = [
          { orderId: 101, productName: 'Wireless Mouse', quantity: 50, status: 'PENDING', orderDate: '2024-03-25' },
          { orderId: 102, productName: 'Mechanical Keyboard', quantity: 20, status: 'SHIPPED', orderDate: '2024-03-26' },
          { orderId: 103, productName: 'Gaming Monitor', quantity: 10, status: 'DELIVERED', orderDate: '2024-03-27' }
        ];
        this.applyFilters();
        this.isLoading = false;
      }
    });
  }

  private applyFilters(): void {
    if (this.isTrackingView) {
      // In tracking view, we prioritize showing what's currently in motion (SHIPPED)
      // or recently delivered, but mostly SHIPPED.
      this.displayOrders = this.orders.filter(o => o.status === 'SHIPPED' || o.status === 'DELIVERED');
    } else {
      this.displayOrders = [...this.orders];
    }
  }

  updateStatus(orderId: number | undefined, currentStatus: string): void {
    if (!orderId) return;
    
    let nextStatus = 'PENDING';
    if (currentStatus === 'PENDING') nextStatus = 'APPROVED';
    else if (currentStatus === 'APPROVED') nextStatus = 'SHIPPED';
    else if (currentStatus === 'SHIPPED') nextStatus = 'DELIVERED';
    else return;

    // Optimistically update UI immediately
    const orderIndex = this.orders.findIndex(o => o.orderId === orderId);
    if (orderIndex !== -1) {
      this.orders[orderIndex].status = nextStatus;
    }

    this.poService.updateOrderStatus(orderId, nextStatus).subscribe({
      next: () => {
        // Successful API
        console.log(`PO ${orderId} status updated to ${nextStatus}`);
      },
      error: (err) => {
        console.warn('Status API failed, but UI state updated locally for demo purposes.', err);
      }
    });
  }

  cancelOrder(orderId: number | undefined, currentStatus: string): void {
    if (!orderId) return;
    if (currentStatus !== 'PENDING' && currentStatus !== 'APPROVED') return;

    const orderIndex = this.orders.findIndex(o => o.orderId === orderId);
    if (orderIndex !== -1) {
      this.orders[orderIndex].status = 'CANCELLED';
      this.applyFilters();
    }

    this.poService.updateOrderStatus(orderId, 'CANCELLED').subscribe({
      next: () => console.log(`PO ${orderId} cancelled`),
      error: (err) => console.warn('Cancel API failed, but UI state updated locally for demo purposes.', err)
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'status-pending';
      case 'APPROVED': return 'status-approved';
      case 'SHIPPED': return 'status-shipped';
      case 'DELIVERED': return 'status-delivered';
      case 'CANCELLED': return 'status-cancelled';
      default: return '';
    }
  }
}
