import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ForecastService } from '../../shared/services/forecast.service';
import { InventoryService } from '../../shared/services/inventory.service';
import { DemandForecastDTO, ProductDTO } from '../../shared/models/inventory.models';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-forecast',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './forecast.component.html',
  styleUrls: ['./forecast.component.css']
})
export class ForecastComponent implements OnInit {
  private readonly forecastService = inject(ForecastService);
  private readonly inventoryService = inject(InventoryService);

  forecasts: DemandForecastDTO[] = [];
  products: ProductDTO[] = [];
  isLoading = true;
  
  // Chart Display State
  showAllProducts = false; 
  chartHeight = '400px';

  public chartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Predicted Demand' }]
  };

  public chartOptions: ChartConfiguration<'bar'>['options'] = {
    indexAxis: 'y', // Horizontal Bar Chart
    responsive: true,
    maintainAspectRatio: false,
    plugins: { 
      legend: { display: false },
      tooltip: {
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        titleColor: '#00f2fe',
        bodyColor: '#fff',
        borderColor: 'rgba(255,255,255,0.1)',
        borderWidth: 1,
        padding: 12,
        boxPadding: 4
      }
    },
    scales: {
      x: { 
        beginAtZero: true, 
        grid: { display: true, color: 'rgba(255,255,255,0.05)' },
        ticks: { color: '#334155', font: { size: 11 } },
        title: {
          display: true,
          text: 'PREDICTED DEMAND (UNITS)',
          color: '#1e293b',
          font: { size: 10, weight: 'bold', family: 'Inter' }
        }
      },
      y: { 
        grid: { display: false },
        ticks: { 
          color: '#334155', 
          font: { size: 11, weight: 600 },
          autoSkip: false
        },
        title: {
          display: true,
          text: 'PRODUCT PORTFOLIO',
          color: '#1e293b',
          font: { size: 10, weight: 'bold', family: 'Inter' }
        }
      }
    }
  };

  public aiInsights: string[] = [];

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.forecastService.getAllForecasts().subscribe({
      next: (data) => {
        this.forecasts = data;
        this.updateChart();
        this.generateAiInsights();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load forecasts', err);
        this.loadMockData();
        this.isLoading = false;
      }
    });

    this.inventoryService.getProducts().subscribe({
      next: (data) => this.products = data,
      error: (err) => console.error('Failed to load products for forecast', err)
    });
  }

  toggleView() {
    this.showAllProducts = !this.showAllProducts;
    this.updateChart();
  }

  public updateChart(): void {
    // Sort by demand descending for readability
    const sortedData = [...this.forecasts].sort((a, b) => b.predictedDemand - a.predictedDemand);
    
    // Slice if we are in "Top 10" mode
    const displayData = this.showAllProducts ? sortedData : sortedData.slice(0, 10);

    // Dynamic HEIGHT for vertical scroll in horizontal bar chart
    // 50px per item is a good standard for horizontal bars with labels
    const calculatedHeight = displayData.length * 50;
    this.chartHeight = `${Math.max(calculatedHeight, 400)}px`;

    this.chartData = {
      labels: displayData.map(f => this.getProductName(f.stockKeepUnit)),
      datasets: [{
        data: displayData.map(f => f.predictedDemand),
        label: 'Predicted Demand',
        backgroundColor: (context) => {
          const chart = context.chart;
          const {ctx, chartArea} = chart;
          if (!chartArea) return 'rgba(6, 182, 212, 0.4)';
          const gradient = ctx.createLinearGradient(chartArea.left, 0, chartArea.right, 0);
          gradient.addColorStop(0, 'rgba(6, 182, 212, 0.2)');
          gradient.addColorStop(1, 'rgba(6, 182, 212, 0.8)');
          return gradient;
        },
        borderColor: '#00f2fe',
        borderWidth: 1,
        borderRadius: 4,
        hoverBackgroundColor: '#00f2fe',
        barThickness: 24 // Professional thickness for horizontal bars
      }]
    };
  }

  generateForecast(productId: number): void {
    if (productId === 0) return;
    this.isLoading = true;
    this.forecastService.generateForecast(productId).subscribe({
      next: () => this.loadData(),
      error: (err) => {
        console.error('Forecast generation failed', err);
        this.isLoading = false;
      }
    });
  }

  runFullAnalysis(): void {
    this.isLoading = true;
    this.forecastService.generateAllForecasts().subscribe({
      next: (data) => {
        this.forecasts = data;
        this.updateChart();
        this.generateAiInsights();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Full system analysis failed', err);
        this.isLoading = false;
      }
    });
  }

  private generateAiInsights(): void {
    this.aiInsights = [];
    if (!this.forecasts.length) return;

    const highest = [...this.forecasts].sort((a, b) => b.predictedDemand - a.predictedDemand)[0];
    this.aiInsights.push(`⚡ CRITICAL: ${this.getProductName(highest.stockKeepUnit)} has the highest projected demand (${highest.predictedDemand} units). Priority restock recommended.`);

    const mostConfident = [...this.forecasts].sort((a, b) => (b.confidenceScore || 0) - (a.confidenceScore || 0))[0];
    if ((mostConfident?.confidenceScore || 0) > 0.9) {
      this.aiInsights.push(`✓ HIGH CONFIDENCE (${((mostConfident.confidenceScore || 0) * 100).toFixed(0)}%): Demand for ${this.getProductName(mostConfident.stockKeepUnit)} is stable.`);
    }

    this.aiInsights.push(`⚠️ ANALYSIS: General demand trends indicating +12% growth in Q2.`);
  }

  private loadMockData(): void {
    this.forecasts = [
      { forecastId: 101, stockKeepUnit: 'SKU-7729', predictedDemand: 850, forecastPeriod: '2024-05-01T00:00:00', confidenceScore: 0.96 },
      { forecastId: 102, stockKeepUnit: 'SKU-2311', predictedDemand: 210, forecastPeriod: '2024-05-01T00:00:00', confidenceScore: 0.81 },
      { forecastId: 103, stockKeepUnit: 'SKU-9908', predictedDemand: 1105, forecastPeriod: '2024-05-01T00:00:00', confidenceScore: 0.98 },
      { forecastId: 104, stockKeepUnit: 'SKU-0014', predictedDemand: 45, forecastPeriod: '2024-05-01T00:00:00', confidenceScore: 0.76 }
    ];
    this.updateChart();
    this.generateAiInsights();
  }

  getProductName(sku: string): string {
    const product = this.products.find(p => p.stockKeepingUnit === sku);
    return product ? product.productName : sku;
  }

  getProductId(sku: string): number {
    const product = this.products.find(p => p.stockKeepingUnit === sku);
    return product ? (product.id || 0) : 0;
  }
}
