import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppContextService } from './app-context.service';
import { DemandForecastDTO } from '../models/inventory.models';

@Injectable({
  providedIn: 'root'
})
export class ForecastService {
  private readonly http = inject(HttpClient);
  private readonly appContext = inject(AppContextService);

  private getUrl(endpoint: string): string {
    return this.appContext.getContext('INVENTORY_CONTEXT') + endpoint;
  }

  /**
   * Fetches all demand forecast records currently stored in the system.
   * Maps to GET /api/forecast/all
   */
  getAllForecasts(): Observable<DemandForecastDTO[]> {
    return this.http.get<DemandForecastDTO[]>(this.getUrl('forecast/all'));
  }

  /**
   * Triggers the calculation of a new demand forecast for a specific product.
   * Maps to POST /api/forecast/generate/{productId}
   */
  generateForecast(productId: number): Observable<DemandForecastDTO> {
    return this.http.post<DemandForecastDTO>(this.getUrl(`forecast/generate/${productId}`), {});
  }

  /**
   * Triggers a system-wide demand analysis for all active products.
   * Maps to POST /api/forecast/generate-all
   */
  generateAllForecasts(): Observable<DemandForecastDTO[]> {
    return this.http.post<DemandForecastDTO[]>(this.getUrl('forecast/generate-all'), {});
  }
}
