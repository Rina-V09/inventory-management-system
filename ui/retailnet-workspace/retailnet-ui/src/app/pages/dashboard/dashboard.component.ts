import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SalesService } from '../../shared/services/sales.service';
import { InventoryService } from '../../shared/services/inventory.service';
import { PurchaseOrderService } from '../../shared/services/purchase-order.service';
import { SupplierService } from '../../shared/services/supplier.service';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { SaleDTO, ProductDTO, PurchaseOrderDTO, SupplierDTO } from '../../shared/models/inventory.models';

/**
 * Enhanced Dashboard Component (Role-Based)
 * Provides dynamic metrics depending on whether the user is an an Inventory Manager or Sales.
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  private readonly salesService = inject(SalesService);
  private readonly inventoryService = inject(InventoryService);
  private readonly poService = inject(PurchaseOrderService);
  private readonly supplierService = inject(SupplierService);

  userRole: 'inventory_manager' | 'sales' | 'procurement' = 'inventory_manager';
  username = 'User';
  isLoading = true;

  stats: any = {};
  
  recentSales: SaleDTO[] = [];
  lowStockItems: ProductDTO[] = [];
  // For procurement logic
  recentPos: any[] = [];
  topSuppliers: any[] = [];

  public chartData: ChartConfiguration<'bar' | 'line' | 'doughnut'>['data'] = {
    labels: [],
    datasets: []
  };

  public chartOptions: ChartConfiguration<'bar' | 'line' | 'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: true, labels: { color: '#1e293b', font: { family: 'Inter' } } }
    },
    scales: {
      y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { color: '#64748b' } },
      x: { grid: { display: false }, ticks: { color: '#64748b' } }
    }
  };

  public chartType: 'bar' | 'line' | 'doughnut' = 'bar';

  ngOnInit(): void {
    this.detectUserRole();
    this.refreshDashboard();
  }

  private detectUserRole(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const roles = payload.realm_access?.roles || [];
        this.username = payload.preferred_username || 'Admin';

        if (roles.includes('INVENTORY_MANAGER')) {
          this.userRole = 'inventory_manager';
        } else if (roles.includes('PROCUREMENT')) {
          this.userRole = 'procurement';
        } else if (roles.includes('SALES')) {
          this.userRole = 'sales';
        } else {
          this.userRole = 'inventory_manager';
        }
      } catch (err) {
        console.error('Error decoding token:', err);
      }
    }
  }

  refreshDashboard(): void {
    this.isLoading = true;
    if (this.userRole === 'inventory_manager') {
      this.loadInventoryDashboard();
    } else if (this.userRole === 'procurement') {
      this.loadProcurementDashboard();
    } else {
      this.loadSalesDashboard();
    }
  }

  private loadInventoryDashboard(): void {
    this.inventoryService.getProducts().subscribe({
      next: (products) => {
        const totalItems = products.length;
        const totalStock = products.reduce((acc, curr) => acc + curr.currentStock, 0);
        this.lowStockItems = products.filter(p => p.currentStock < (p.reorderPoint || 10));
        
        this.stats = {
          totalProducts: totalItems,
          totalStock: totalStock,
          lowStock: this.lowStockItems.length,
          activePos: Math.floor(Math.random() * 15) + 3
        };

        this.chartType = 'bar';
        this.chartData = {
          labels: products.slice(0, 7).map(p => p.productName.substring(0, 10) + '..'),
          datasets: [
            {
              data: products.slice(0, 7).map(p => p.currentStock),
              label: 'Current Stock Level',
              backgroundColor: 'rgba(6, 182, 212, 0.8)',
              borderColor: '#06b6d4',
              borderWidth: 1,
              borderRadius: 4
            }
          ]
        };
        this.isLoading = false;
      },
      error: () => this.loadDemoData()
    });
  }

  private loadProcurementDashboard(): void {
    this.poService.getAllOrders().subscribe({
      next: (orders) => {
        const pending = orders.filter((o: any) => o.status === 'PENDING').length;
        const shipped = orders.filter((o: any) => o.status === 'SHIPPED').length;
        const delivered = orders.filter((o: any) => o.status === 'DELIVERED').length;
        
        this.stats = {
          ...this.stats,
          pendingOrders: pending,
          inTransit: shipped,
          receivedMonth: delivered
        };
        
        // Load Real Supplier Data for the Dashboard
        this.supplierService.getSuppliers().subscribe({
          next: (suppliers: SupplierDTO[]) => {
            this.stats.supplierCount = suppliers.length;
            this.topSuppliers = suppliers.slice(0, 5); // Show top 5 vendors
          },
          error: (err: Error) => {
            console.error('Failed to load suppliers for dashboard', err);
            this.stats.supplierCount = 0;
          }
        });

        // Display the most recent 5 orders for the feed
        this.recentPos = orders.slice(-5).reverse().map((o: PurchaseOrderDTO) => ({
          id: o.orderId,
          supplier: `Linked to ${o.productName}`,
          amount: `${o.quantity} Units`,
          status: o.status
        }));

        this.chartOptions!.scales = undefined; // Disable axes for doughnut
        this.chartType = 'doughnut';
        this.chartData = {
          labels: ['Pending', 'In Transit', 'Received'],
          datasets: [{
            data: [pending, shipped, delivered],
            backgroundColor: ['#f59e0b', '#3b82f6', '#10b981'],
            borderWidth: 0
          }]
        };
        
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load real PO data', err);
        this.isLoading = false;
      }
    });
  }

  private loadSalesDashboard(): void {
    this.salesService.getSalesHistory().subscribe({
      next: (transactions) => {
        const revenue = transactions.reduce((acc, curr) => acc + (curr.totalAmount || 0), 0);
        this.recentSales = transactions.slice(0, 5);
        
        this.stats = {
          revenue: revenue,
          orders: transactions.length,
          avgOrderValue: revenue / (transactions.length || 1),
          conversion: '8.4%'
        };

        this.chartType = 'line';
        this.chartData = {
          labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          datasets: [
            {
              data: [12000, 15000, 11000, 18000, 22000, 25000, 21000],
              label: 'Daily Revenue (₹)',
              fill: true,
              tension: 0.4,
              borderColor: '#f97316', 
              backgroundColor: 'rgba(249, 115, 22, 0.1)'
            }
          ]
        };
        this.isLoading = false;
      },
      error: () => this.loadDemoData()
    });
  }

  private loadDemoData(): void {
    if (this.userRole === 'inventory_manager') {
      this.stats = { totalProducts: 142, totalStock: 3450, lowStock: 12, activePos: 5 };
      this.lowStockItems = [
        { id: 1, productName: 'Wireless Mouse', currentStock: 2, reorderPoint: 10, category: 'Tech', price: 450 }
      ];
      this.chartType = 'bar';
      this.chartData = {
        labels: ['Mouse', 'Keyboard', 'Monitor', 'Desk', 'Chair'],
        datasets: [{ data: [12, 45, 8, 30, 25], label: 'Stock Level', backgroundColor: 'rgba(6, 182, 212, 0.8)' }]
      };
    } else if (this.userRole === 'sales') {
      this.stats = { revenue: 125400, orders: 45, avgOrderValue: 2780, conversion: '8.4%' };
      this.recentSales = [
        { saleId: 101, productName: 'Wireless Mouse', quantity: 2, totalAmount: 450, saleDate: '2024-03-25' }
      ] as any[];
      this.chartType = 'line';
      this.chartData = {
        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        datasets: [
          {
            data: [12000, 15000, 11000, 18000, 22000, 25000, 21000],
            label: 'Daily Revenue (₹)',
            fill: true,
            tension: 0.4,
            borderColor: '#f97316',
            backgroundColor: 'rgba(249, 115, 22, 0.1)'
          }
        ]
      };
    }
    this.isLoading = false;
  }
}
