import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppContextService } from './app-context.service';
import { Observable } from 'rxjs';
import { SaleDTO } from '../models/inventory.models';

@Injectable({ providedIn: 'root' })
export class SalesService {
    /** HttpClient for sales data retrieval */
    private readonly http = inject(HttpClient);

    /** Context service for resolving sales API base URLs */
    private readonly appContext = inject(AppContextService);


    private getUrl(endpoint: string): string {
        return this.appContext.getContext('INVENTORY_CONTEXT') + endpoint;
    }

    /**
     * Retrieves the complete sales history from the backend.
     * Maps to GET /api/sales/history
     */
    getSalesHistory(): Observable<SaleDTO[]> {
        return this.http.get<SaleDTO[]>(this.getUrl('sales/history'));
    }

    /**
     * Executes a sale transaction for a specific product.
     * Maps to POST /api/sales/record?productId={id}&quantity={qty}
     */
    recordSale(productId: number, quantity: number): Observable<void> {
        const url = `${this.getUrl('sales/record')}?productId=${productId}&quantity=${quantity}`;
        return this.http.post<void>(url, {});
    }
}