import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface SystemHealth {
  service: string;
  status: 'ONLINE' | 'OFFLINE' | 'DEGRADED';
  latency: string;
  lastCheck: Date;
}

interface AuditLog {
  id: string;
  user: string;
  action: string;
  timestamp: Date;
  severity: 'INFO' | 'WARN' | 'CRITICAL';
}

@Component({
  selector: 'app-it-support',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './it-support.component.html',
  styleUrls: ['./it-support.component.css']
})
export class ITSupportComponent implements OnInit {
  private readonly http = inject(HttpClient);

  healthMetrics: SystemHealth[] = [
    { service: 'Inventory API (8081)', status: 'ONLINE', latency: '45ms', lastCheck: new Date() },
    { service: 'Authentication System (8080)', status: 'ONLINE', latency: '120ms', lastCheck: new Date() },
    { service: 'ERP Connector Service', status: 'DEGRADED', latency: '1.2s', lastCheck: new Date() },
    { service: 'Database Cluster', status: 'ONLINE', latency: '8ms', lastCheck: new Date() }
  ];

  auditLogs: AuditLog[] = [
    { id: 'LOG-001', user: 'admin', action: 'System Login', timestamp: new Date(), severity: 'INFO' },
    { id: 'LOG-002', user: 'procurement_lead', action: 'Manual PO Override', timestamp: new Date(Date.now() - 3600000), severity: 'WARN' },
    { id: 'LOG-003', user: 'system', action: 'ERP Sync Failed (Timeout)', timestamp: new Date(Date.now() - 7200000), severity: 'CRITICAL' },
    { id: 'LOG-004', user: 'sales_user', action: 'Demand Forecast Run', timestamp: new Date(Date.now() - 10800000), severity: 'INFO' }
  ];

  erpSyncProgress = 85;

  ngOnInit(): void {
    console.debug('[IT-SUPPORT] Initializing Enterprise Health Monitoring...');
  }

  refreshHealth() {
    this.healthMetrics = this.healthMetrics.map(h => ({
      ...h,
      lastCheck: new Date(),
      latency: Math.floor(Math.random() * 200) + 'ms'
    }));
  }
}
