import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SupplierService } from '../../shared/services/supplier.service';
import { SupplierDTO } from '../../shared/models/inventory.models';
import { NotificationService } from '../../shared/services/notification.service';

@Component({
  selector: 'app-suppliers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './suppliers.component.html',
  styleUrls: ['./suppliers.component.css']
})
export class SuppliersComponent implements OnInit {
  private readonly supplierService = inject(SupplierService);
  private readonly notificationService = inject(NotificationService);

  suppliers: SupplierDTO[] = [];
  isLoading = true;
  showAddModal = false;
  showEditModal = false;
  showDeleteModal = false;
  
  isSaving = false;
  isDeleting = false;
  errorMessage: string | null = null;

  currentSupplier: SupplierDTO = this.getEmptySupplier();
  deleteId: number | null = null;

  ngOnInit(): void {
    this.loadSuppliers();
  }

  getEmptySupplier(): SupplierDTO {
    return {
      supplierName: '',
      contactEmail: '',
      category: 'Electronics',
      rating: 'EXCELLENT',
      leadTimeDays: 5
    };
  }

  loadSuppliers(): void {
    this.isLoading = true;
    this.supplierService.getSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load suppliers', err);
        this.isLoading = false;
        this.notificationService.error('Could not fetch supplier list. Please check your connection.');
      }
    });
  }

  openAddModal(): void {
    this.showAddModal = true;
    this.currentSupplier = this.getEmptySupplier();
    this.errorMessage = null;
  }

  openEditModal(supplier: SupplierDTO): void {
    this.showEditModal = true;
    this.currentSupplier = { ...supplier };
    this.errorMessage = null;
  }

  openDeleteModal(id: number | undefined): void {
    if (id) {
      this.deleteId = id;
      this.showDeleteModal = true;
    }
  }

  saveSupplier(): void {
    if (!this.currentSupplier.supplierName || !this.currentSupplier.contactEmail) return;

    this.isSaving = true;
    this.supplierService.addSupplier(this.currentSupplier).subscribe({
      next: () => {
        this.notificationService.success('Supplier registered successfully.');
        this.loadSuppliers();
        this.showAddModal = false;
        this.isSaving = false;
      },
      error: (err) => {
        console.error('Save failed', err);
        this.isSaving = false;
        this.notificationService.error('Failed to register vendor. Please check all fields.');
      }
    });
  }

  updateSupplier(): void {
    if (!this.currentSupplier.supplierId || !this.currentSupplier.supplierName) return;

    this.isSaving = true;
    this.supplierService.updateSupplier(this.currentSupplier.supplierId, this.currentSupplier).subscribe({
      next: () => {
        this.notificationService.success('Supplier updated successfully.');
        this.loadSuppliers();
        this.showEditModal = false;
        this.isSaving = false;
      },
      error: (err) => {
        console.error('Update failed', err);
        this.isSaving = false;
        this.notificationService.error('Failed to update vendor record.');
      }
    });
  }

  confirmDelete(): void {
    if (!this.deleteId) return;

    this.isDeleting = true;
    this.supplierService.deleteSupplier(this.deleteId).subscribe({
      next: () => {
        this.notificationService.success('Supplier removed successfully.');
        this.loadSuppliers();
        this.showDeleteModal = false;
        this.isDeleting = false;
        this.deleteId = null;
      },
      error: (err) => {
        console.error('Delete failed', err);
        this.isDeleting = false;
        this.notificationService.error('Failed to remove supplier. There may be linked products.');
      }
    });
  }
}
