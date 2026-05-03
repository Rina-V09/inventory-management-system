import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InventoryComponent } from './inventory.component';
import { InventoryService } from '../../shared/services/inventory.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { SupplierService } from '../../shared/services/supplier.service';
import { NotificationService } from '../../shared/services/notification.service';

describe('InventoryComponent', () => {
  let component: InventoryComponent;
  let fixture: ComponentFixture<InventoryComponent>;
  let mockInventoryService: any;
  let mockSupplierService: any;
  let mockRouter: any;
  let mockNotificationService: any;

  beforeEach(async () => {
    localStorage.setItem('token', 'test.header.payload');

    mockInventoryService = {
      getProducts: jest.fn().mockReturnValue(of([])),
      addProduct: jest.fn(),
      updateProduct: jest.fn(),
      deleteProduct: jest.fn(),
      triggerRestock: jest.fn().mockReturnValue(of(void 0)),
    };

    mockSupplierService = {
      getSuppliers: jest.fn().mockReturnValue(of([])),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    mockNotificationService = {
      info: jest.fn(),
      success: jest.fn(),
      warn: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [
        InventoryComponent, 
        HttpClientTestingModule,
        FormsModule
      ],
      providers: [
        { provide: InventoryService, useValue: mockInventoryService },
        { provide: SupplierService, useValue: mockSupplierService },
        { provide: Router, useValue: mockRouter },
        { provide: NotificationService, useValue: mockNotificationService },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Trigger ngOnInit
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open the modal when openAddModal is called', () => {
    component.openAddModal();
    expect(component.showModal).toBe(true);
    expect(component.isEditing).toBe(false);
  });

  it('should filter products when onSearch is triggered', () => {
    // Setup dummy data
    component.products = [
      { productName: 'Laptop', category: 'Electronics' } as any,
      { productName: 'Chair', category: 'Furniture' } as any
    ];
    
    const event = { target: { value: 'lap' } };
    component.onSearch(event);
    
    expect(component.filteredProducts.length).toBe(1);
    expect((component.filteredProducts[0] as any).productName).toBe('Laptop');
  });
});