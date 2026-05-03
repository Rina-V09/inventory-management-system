import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppContextService } from './app-context.service';
import { SupplierDTO } from '../models/inventory.models';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  private readonly http = inject(HttpClient);
  private readonly appContext = inject(AppContextService);

  private readonly httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  private getUrl(endpoint: string): string {
    return this.appContext.getContext('INVENTORY_CONTEXT') + endpoint;
  }

  /**
   * Retrieves a comprehensive list of all currently registered suppliers.
   * Used for population of vendor selection lists and reporting.
   * Maps to GET /api/suppliers/all
   */
  getSuppliers(): Observable<SupplierDTO[]> {
    return this.http.get<SupplierDTO[]>(this.getUrl('suppliers/all'));
  }

  /**
   * Registers a new supplier in the system.
   * Consumes JSON data to create a persistent supplier record.
   * Maps to POST /api/suppliers/add
   */
  addSupplier(supplier: SupplierDTO): Observable<SupplierDTO> {
    return this.http.post<SupplierDTO>(this.getUrl('suppliers/add'), supplier, this.httpOptions);
  }

  /**
   * Updates an existing supplier's information.
   * Maps to PUT /api/suppliers/update/{id}
   */
  updateSupplier(id: number, supplier: SupplierDTO): Observable<SupplierDTO> {
    return this.http.put<SupplierDTO>(this.getUrl(`suppliers/update/${id}`), supplier, this.httpOptions);
  }

  /**
   * Removes a supplier from the system.
   * Maps to DELETE /api/suppliers/delete/{id}
   */
  deleteSupplier(id: number): Observable<void> {
    return this.http.delete<void>(this.getUrl(`suppliers/delete/${id}`));
  }
}
