import { Pipe, PipeTransform } from '@angular/core';
import { PurchaseOrderDTO } from '../models/inventory.models';

@Pipe({
  name: 'filterStatus',
  standalone: true
})
export class FilterStatusPipe implements PipeTransform {
  transform(orders: PurchaseOrderDTO[], status: string): PurchaseOrderDTO[] {
    if (!orders) return [];
    return orders.filter(order => order.status === status);
  }
}
