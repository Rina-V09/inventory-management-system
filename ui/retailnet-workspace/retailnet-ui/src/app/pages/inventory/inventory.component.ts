import { Component, OnInit, HostListener, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { InventoryService } from '../../shared/services/inventory.service';
import { SupplierService } from '../../shared/services/supplier.service';
import { NotificationService } from '../../shared/services/notification.service';
import { ProductDTO, SupplierDTO } from '../../shared/models/inventory.models';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css',
})
export class InventoryComponent implements OnInit {
  /** Service for handling all inventory related API operations */
  private readonly inventoryService = inject(InventoryService);
  private readonly supplierService = inject(SupplierService);

  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);
  
  products: ProductDTO[] = [];
  filteredProducts: ProductDTO[] = [];
  suppliers: SupplierDTO[] = [];
  
  // UI State Management
  showModal = false;
  isEditing = false;
  isScrolled = false; // Controls the header shadow
  isLoading = false;  // Visual feedback for API calls
  
  currentProduct: ProductDTO = { productName: '', category: '', price: 0, currentStock: 0, reorderPoint: 10 };

  /**
   * Lifecycle hook that is called after data-bound properties of a directive are initialized
   */
  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('Unauthorized access. Redirecting to login...');
      this.router.navigate(['/login']);
      return;
    }
    this.loadInventory();
    this.loadSuppliers();
  }

  // --- Sticky Header Logic ---
  // This listens to the window scroll and toggles a class for the CSS
  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 20;
  }

  /**
   * Fetches all products from the inventory service and updates the UI state
   */
  loadInventory(): void {
    this.isLoading = true;
    this.inventoryService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.filteredProducts = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to fetch products from 8081:', err);
        this.isLoading = false;
      }
    });
  }

  /**
   * Fetches all registered suppliers for the dropdown list in the modal
   */
  loadSuppliers(): void {
    this.supplierService.getSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
      },
      error: (err) => {
        console.error('Failed to fetch suppliers:', err);
      }
    });
  }

  /**
   * Scans for all products below reorder point and generates Purchase Orders
   */
  triggerAutoRestock(): void {
    this.isLoading = true;
    this.notificationService.info('Scanning inventory for low-stock items...', 'Stock Scan Started');
    this.inventoryService.triggerRestock().subscribe({
      next: () => {
        this.notificationService.success('Purchase Orders have been generated for all low-stock items.', 'Scan Complete');
        this.isLoading = false;
        this.loadInventory();
      },
      error: (err) => {
        this.notificationService.error('Restock scan failed: ' + err.message);
        this.isLoading = false;
      }
    });
  }

  /**
   * Filters the product list based on the search term provided in the input field
   * @param event The input event containing the search term
   */
  onSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    const term = input.value.toLowerCase();
    this.filteredProducts = this.products.filter(p => 
      (p.productName?.toLowerCase() || '').includes(term) || 
      (p.category?.toLowerCase() || '').includes(term)
    );
  }

  // --- Modal Controls ---
  openAddModal() {
    this.isEditing = false;
    this.currentProduct = { productName: '', category: '', price: 0, currentStock: 0, reorderPoint: 10 };
    this.showModal = true;
  }

  openEditModal(product: ProductDTO) {
    this.isEditing = true;
    this.currentProduct = { ...product }; // Shallow copy to prevent immediate UI sync
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  /**
   * Saves the current product state (either creating a new one or updating an existing one)
   */
  saveProduct(form: NgForm) {
    if (form.invalid) {
      Object.values(form.controls).forEach((c) => {
        c.markAsTouched();
      });
      this.notificationService.warn('Please fix the highlighted fields.', 'Validation');
      return;
    }

    if (!this.currentProduct.supplierName?.trim()) {
      this.notificationService.warn('Please select a supplier.', 'Validation');
      return;
    }

    this.isLoading = true;
    const observer = {
      next: () => {
        this.notificationService.success(`Product ${this.isEditing ? 'updated' : 'added'} successfully.`);
        this.loadInventory(); // Refresh the table
        this.closeModal();
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.notificationService.error('Operation failed: ' + err.message);
        this.isLoading = false;
      }
    };

    if (this.isEditing && this.currentProduct.id !== undefined) {
      this.inventoryService.updateProduct(this.currentProduct.id, this.currentProduct).subscribe(observer);
    } else {
      if (!this.currentProduct.stockKeepingUnit) {
        this.currentProduct.stockKeepingUnit = 'SKU-' + Math.floor(Math.random() * 10000) + '-' + Date.now().toString().slice(-4);
      }
      this.inventoryService.addProduct(this.currentProduct).subscribe(observer);
    }
  }

  showDeleteModal = false;
  deleteProductId: number | null = null;

  /**
   * Prompts the user with a custom UI modal to confirm deletion
   * @param id The unique identifier of the product to delete
   */
  deleteProduct(id: number) {
    this.deleteProductId = id;
    this.showDeleteModal = true;
  }

  /**
   * Executes the deletion via the HTTP service after user confirmation
   */
  confirmDelete() {
    if (this.deleteProductId !== null) {
      this.isLoading = true;
      this.inventoryService.deleteProduct(this.deleteProductId).subscribe({
        next: () => {
          this.notificationService.success('Product Deleted successfully.');
          this.loadInventory();
          this.closeDeleteModal();
          this.isLoading = false;
        },
        error: (err) => {
          this.notificationService.error('Delete failed: ' + err.message);
          this.closeDeleteModal();
          this.isLoading = false;
        }
      });
    }
  }

  /**
   * Closes the delete confirmation modal
   */
  closeDeleteModal() {
    this.showDeleteModal = false;
    this.deleteProductId = null;
  }
}