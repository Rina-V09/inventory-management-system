export interface ProductDTO {
  id?: number;
  productName: string;
  stockKeepingUnit?: string;
  category: string;
  price: number;
  currentStock: number;
  reorderPoint?: number;
  supplierName?: string;
  totalSalesCount?: number;
}

export interface PurchaseOrderDTO {
  orderId?: number;
  productName: string;
  quantity: number;
  status: string; // PENDING, SENT, RECEIVED, etc.
  orderDate?: string;
  supplierName?: string;
  itemCount?: number;
  items?: PurchaseOrderItemDTO[];
}

export interface PurchaseOrderItemDTO {
  productId: string;
  quantityOrdered: number;
}

export interface SupplierDTO {
  supplierId?: number;
  supplierName: string;
  contactEmail: string;
  category: string;
  rating: string;
  leadTimeDays?: number;
}

export interface SaleDTO {
  saleId?: number;
  productName: string;
  quantity: number;
  totalAmount: number;
  saleDate?: string;
  findTotalSalesLastMonth?: number;
}

export interface DemandForecastDTO {
  forecastId?: number;
  stockKeepUnit: string;
  predictedDemand: number;
  forecastPeriod: string;
  confidenceScore?: number;
}

export enum NotificationType {
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
  WARNING = 'WARNING',
  INFO = 'INFO'
}

export interface AppNotification {
  id: number;
  type: NotificationType;
  message: string;
  title?: string;
  duration?: number;
}
