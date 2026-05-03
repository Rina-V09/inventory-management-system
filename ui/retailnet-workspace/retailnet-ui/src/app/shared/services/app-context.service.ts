import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AppContextService {
  /** HttpClient for loading global configuration files */
  private readonly http = inject(HttpClient);

  private config: any = null;


  loadConfig(): Promise<any> {
    const configPath = 'assets/data/app-context.json';

    return firstValueFrom(
      this.http.get(configPath).pipe(
        tap((data) => {
          this.config = data;
          console.log('Context Loaded Successfully', this.config);
        })
      )
    ).catch((err) => {
      console.error(`Pipeline Failed: Cannot find ${configPath}.`, err);
    });
  }

  getContext(key: string): string {
    if (!this.config) {
      console.warn(`getContext('${key}') called before config was loaded!`);
      return '';
    }
    return this.config[key] || '';
  }
}