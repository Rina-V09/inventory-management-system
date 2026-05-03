import { Route } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { MainLayoutComponent } from './shared/layouts/main-layout';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { InventoryComponent } from './pages/inventory/inventory.component';
import { SalesComponent } from './pages/sales/sales.component';
import { PurchaseOrdersComponent } from './pages/purchase-orders/purchase-orders.component';
import { SuppliersComponent } from './pages/suppliers/suppliers.component';
import { ForecastComponent } from './pages/forecast/forecast.component';


export const appRoutes: Route[] = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'inventory', component: InventoryComponent },
      { path: 'sales', component: SalesComponent },
      { path: 'purchase-orders', component: PurchaseOrdersComponent },
      { path: 'suppliers', component: SuppliersComponent },
      { path: 'forecast', component: ForecastComponent },
      { path: 'it-support', loadComponent: () => import('./pages/it-support/it-support.component').then(c => c.ITSupportComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];