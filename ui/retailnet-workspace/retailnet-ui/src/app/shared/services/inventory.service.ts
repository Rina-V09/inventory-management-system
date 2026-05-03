import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppContextService } from './app-context.service';
import { ProductDTO } from '../models/inventory.models';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  /** HttpClient for data fetching and CRUD operations */
  private readonly http = inject(HttpClient);

  /** Context service for dynamic URL resolution */
  private readonly appContext = inject(AppContextService);


  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  /**
   * Helper method to build the full URL dynamically.
   * This ensures we always have the latest value from the AppContext.
   */
  private getFullUrl(endpoint: string): string {
    const base = this.appContext.getContext('INVENTORY_CONTEXT');
    console.log(`Building URL for ${endpoint}:`, base + endpoint);
    return base + endpoint;
  }

  getProducts(): Observable<ProductDTO[]> {
    return this.http.get<ProductDTO[]>(this.getFullUrl('products/all'));
  }

  addProduct(product: ProductDTO): Observable<ProductDTO> {
    return this.http.post<ProductDTO>(this.getFullUrl('products/add'), product, this.httpOptions);
  }

  updateProduct(id: number, product: ProductDTO): Observable<ProductDTO> {
    return this.http.put<ProductDTO>(this.getFullUrl(`products/update/${id}`), product, this.httpOptions);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(this.getFullUrl(`products/delete/${id}`), { responseType: 'text' });
  }

  /**
   * Manually triggers the inventory replenishment logic.
   * Maps to POST /api/inventory/trigger-restock
   */
  triggerRestock(): Observable<void> {
    return this.http.post<void>(this.getFullUrl('inventory/trigger-restock'), {});
  }
}