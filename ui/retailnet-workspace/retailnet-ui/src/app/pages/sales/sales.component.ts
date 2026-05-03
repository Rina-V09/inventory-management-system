import { Component, OnInit, ElementRef, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SalesService } from '../../shared/services/sales.service';
import { SaleDTO } from '../../shared/models/inventory.models';
import { BaseChartDirective } from 'ng2-charts'; 
import { ChartConfiguration, ChartOptions, ChartType } from 'chart.js';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);
import jsPDF from 'jspdf';
//highcharts
import autoTable from 'jspdf-autotable';

@Component({
    selector: 'app-sales',
    standalone: true,
    imports: [CommonModule, BaseChartDirective],
    templateUrl: './sales.component.html',
    styleUrl: './sales.component.css'
})

export class SalesComponent implements OnInit {
    /** Service for handling sales transactions and reporting */
    private readonly salesService = inject(SalesService);

    @ViewChild('datePicker') datePicker!: ElementRef;
    today = new Date();

    openCalendar() {
        this.datePicker.nativeElement.showPicker();
      }
      onDateChange(event: any) {
        const selectedDate = new Date(event.target.value);
        if (selectedDate) {
          this.today = selectedDate;
          console.log('Fetching data for:', selectedDate);
        }
      }
    stats: { revenue: number; orders: number; customers: number } = { revenue: 0, orders: 0, customers: 0 };
    transactions: SaleDTO[] = [];
    chartHasData = false;

    /**
     * Lifecycle hook that is called after data-bound properties are initialized
     */
    ngOnInit(): void {
        this.loadSalesData();
    }
    // Populated from API in loadSalesData (last 7 days revenue buckets)
    public lineChartData: ChartConfiguration<'line'>['data'] = {
        labels: [],
        datasets: [
            {
                data: [],
                label: 'Revenue (₹)',
                fill: true,
                tension: 0.35,
                borderColor: '#38bdf8',
                backgroundColor: 'rgba(56, 189, 248, 0.2)',
                borderWidth: 2,
                pointRadius: 4,
                pointBackgroundColor: '#38bdf8',
                pointBorderColor: '#0f172a',
                pointBorderWidth: 1
            }]
        };

    public lineChartOptions: ChartConfiguration<'line'>['options'] = {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { intersect: false, mode: 'index' },
        plugins: {
            legend: { display: true, labels: { color: '#94a3b8' } },
            tooltip: {
                backgroundColor: 'rgba(15, 23, 42, 0.95)',
                titleColor: '#f1f5f9',
                bodyColor: '#e2e8f0'
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                grid: { color: 'rgba(148, 163, 184, 0.15)' },
                ticks: { color: '#94a3b8' }
            },
            x: {
                grid: { display: false },
                ticks: { color: '#94a3b8', maxRotation: 45, minRotation: 0 }
            }
        }
    };

    /**
     * Fetches recent sales transactions from the API
     */
    loadSalesData() {
        this.salesService.getSalesHistory().subscribe({
            next: (data) => {
                this.transactions = data;
                this.applyStatsAndChart(data);
            },
            error: (err) => {
                console.error('Sales API failed, using mock data', err);
                const today = new Date().toISOString().slice(0, 10);
                this.transactions = [
                    { saleId: 101, productName: 'Laptop', quantity: 1, totalAmount: 55000, saleDate: today }
                ];
                this.applyStatsAndChart(this.transactions);
            }
        });
    }

    /**
     * Binds KPI cards and the line chart to real sales rows (last 7 calendar days by revenue).
     */
    private applyStatsAndChart(rows: SaleDTO[]): void {
        const revenue = rows.reduce((a, t) => a + (t.totalAmount || 0), 0);
        const productKeys = new Set(rows.map((r) => r.productName).filter(Boolean));
        this.stats = {
            revenue,
            orders: rows.length,
            customers: productKeys.size
        };

        const { labels, amounts } = this.aggregateRevenueLast7Days(rows);
        const maxVal = Math.max(...amounts, 1);
        this.chartHasData = amounts.some((v) => v > 0);

        this.lineChartData = {
            labels,
            datasets: [
                {
                    data: amounts,
                    label: 'Revenue (₹)',
                    fill: true,
                    tension: 0.35,
                    borderColor: '#38bdf8',
                    backgroundColor: 'rgba(56, 189, 248, 0.2)',
                    borderWidth: 2,
                    pointRadius: 4,
                    pointBackgroundColor: '#38bdf8',
                    pointBorderColor: '#0f172a',
                    pointBorderWidth: 1
                }
            ]
        };

        this.lineChartOptions = {
            ...this.lineChartOptions,
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { color: '#94a3b8', maxRotation: 45, minRotation: 0 }
                },
                y: {
                    beginAtZero: true,
                    suggestedMax: maxVal * 1.1,
                    grid: { color: 'rgba(148, 163, 184, 0.15)' },
                    ticks: { color: '#94a3b8' }
                }
            }
        };
    }

    private toYmd(d: Date): string {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${y}-${m}-${day}`;
    }

    private saleRowYmd(t: SaleDTO): string | null {
        const raw = t.saleDate;
        if (raw == null) {
            return null;
        }
        if (typeof raw === 'string') {
            return raw.length >= 10 ? raw.slice(0, 10) : null;
        }
        if (typeof raw === 'object' && raw !== null && '0' in (raw as object)) {
            const a = raw as number[];
            if (a.length >= 3) {
                return `${a[0]}-${String(a[1]).padStart(2, '0')}-${String(a[2]).padStart(2, '0')}`;
            }
        }
        return null;
    }

    private aggregateRevenueLast7Days(rows: SaleDTO[]): { labels: string[]; amounts: number[] } {
        const labels: string[] = [];
        const keys: string[] = [];
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        for (let i = 6; i >= 0; i--) {
            const d = new Date(today);
            d.setDate(today.getDate() - i);
            keys.push(this.toYmd(d));
            labels.push(
                d.toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric' })
            );
        }
        const perDay = new Map<string, number>();
        for (const k of keys) {
            perDay.set(k, 0);
        }
        for (const t of rows) {
            const ymd = this.saleRowYmd(t);
            if (ymd && perDay.has(ymd)) {
                perDay.set(ymd, (perDay.get(ymd) || 0) + (t.totalAmount || 0));
            }
        }
        const amounts = keys.map((k) => perDay.get(k) || 0);
        return { labels, amounts };
    }
    
    /**
     * Generates and downloads a PDF report of the current sales transactions
     */
    exportToPDF() {
        
        const doc = new jsPDF();
        
        doc.setFontSize(20);
        doc.setTextColor(40, 41, 59); 
        doc.text('RetailNet - Sales Report', 14, 22);
        
        doc.setFontSize(11);
        doc.setTextColor(100);
        doc.text(`Generated on: ${this.today.toLocaleString()}`, 14, 30);
        doc.text(`Total Revenue: INR ${this.stats.revenue}`, 14, 38);

  
        const tableData = this.transactions.map(t => [
            `#${t.saleId}`,
            t.productName || 'N/A',
            t.quantity || 0,
            `INR ${t.totalAmount}`,
            'COMPLETED']);
            
        autoTable(doc, {
            
            startY: 45,
            head: [['Sale ID', 'Product', 'Quantity', 'Total Amount', 'Status']],
            body: tableData,
            theme: 'striped',
            headStyles: { fillColor: [30, 41, 59] },
            styles: { fontSize: 10, cellPadding: 5 }
        });
        
        doc.save(`Sales_Report_${new Date().getTime()}.pdf`);
}
}