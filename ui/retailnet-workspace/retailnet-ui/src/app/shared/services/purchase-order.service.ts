import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppContextService } from './app-context.service';
import { PurchaseOrderDTO } from '../models/inventory.models';


@Injectable({
  providedIn: 'root'
})
export class PurchaseOrderService {
  private readonly http = inject(HttpClient);
  private readonly appContext = inject(AppContextService);

  private readonly httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  private getUrl(endpoint: string): string {
    return this.appContext.getContext('INVENTORY_CONTEXT') + endpoint;
  }

  /**
   * Fetches all purchase orders from the backend.
   * Maps to GET /api/orders/all
   */
  getAllOrders(): Observable<PurchaseOrderDTO[]> {
    return this.http.get<PurchaseOrderDTO[]>(this.getUrl('orders/all'));
  }

  /**
   * Fetches a single purchase order by ID.
   * Maps to GET /api/orders/{id}
   */
  getOrderById(id: number): Observable<PurchaseOrderDTO> {
    return this.http.get<PurchaseOrderDTO>(this.getUrl(`orders/${id}`));
  }

  /**
   * Updates the status of an existing order (e.g., PENDING -> RECEIVED).
   * Maps to PUT /api/orders/{id}/status?status={status}
   */
  updateOrderStatus(id: number, status: string): Observable<PurchaseOrderDTO> {
    const url = `${this.getUrl(`orders/${id}/status`)}?status=${status}`;
    return this.http.put<PurchaseOrderDTO>(url, {}, this.httpOptions);
  }

  /**
   * Creates a new purchase order with multiple line items.
   * Maps to POST /api/orders/create
   */
  createOrder(request: { supplierId: number; productIds: number[]; quantities: number[] }): Observable<PurchaseOrderDTO> {
    return this.http.post<PurchaseOrderDTO>(this.getUrl('orders/create'), request, this.httpOptions);
  }
}
