package com.retailnet.inventory.config;

import com.retailnet.inventory.constant.SupplierRating;
import com.retailnet.inventory.entity.Product;
import com.retailnet.inventory.entity.Supplier;
import com.retailnet.inventory.repository.ProductRepository;
import com.retailnet.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * DataInitializer ensures that the database is not empty during development.
 * It checks for existing products and populates the database with mock 
 * data if no records are found.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Database is empty. Initializing mock inventory data...");

            // 1. Create Default Suppliers
            Supplier s1 = new Supplier();
            s1.setSupplierName("Electro-Global Ltd.");
            s1.setCategory("Electronics");
            s1.setContactEmail("supply@electroglobal.com");
            s1.setRating(SupplierRating.EXCELLENT);
            s1.setLeadTimeDays(3);

            Supplier s2 = new Supplier();
            s2.setSupplierName("HomeStyle Solutions");
            s2.setCategory("Furniture");
            s2.setContactEmail("sales@homestyle.com");
            s2.setRating(SupplierRating.GOOD);
            s2.setLeadTimeDays(7);

            supplierRepository.saveAll(Arrays.asList(s1, s2));

            // 2. Create Sample Products
            Product p1 = new Product();
            p1.setProductName("UltraBook Pro X");
            p1.setStockKeepingUnit("SKU-LAP-001");
            p1.setCategory("Electronics");
            p1.setPrice(85000.0);
            p1.setCurrentStock(45);
            p1.setReorderPoint(15);
            p1.setSupplier(s1);

            Product p2 = new Product();
            p2.setProductName("Ergo-Chair Evolution");
            p2.setStockKeepingUnit("SKU-FUR-002");
            p2.setCategory("Furniture");
            p2.setPrice(12500.0);
            p2.setCurrentStock(12);
            p2.setReorderPoint(10);
            p2.setSupplier(s2);

            Product p3 = new Product();
            p3.setProductName("Noise-Cancelling Headphones");
            p3.setStockKeepingUnit("SKU-ACC-003");
            p3.setCategory("Electronics");
            p3.setPrice(18000.0);
            p3.setCurrentStock(5);
            p3.setReorderPoint(8); // Low Stock Trigger
            p3.setSupplier(s1);

            Product p4 = new Product();
            p4.setProductName("Mechanical Keyboard G1");
            p4.setStockKeepingUnit("SKU-ACC-004");
            p4.setCategory("Electronics");
            p4.setPrice(7500.0);
            p4.setCurrentStock(60);
            p4.setReorderPoint(20);
            p4.setSupplier(s1);

            Product p5 = new Product();
            p5.setProductName("Standing Desk Flex");
            p5.setStockKeepingUnit("SKU-FUR-005");
            p5.setCategory("Furniture");
            p5.setPrice(22000.0);
            p5.setCurrentStock(0); // Out of Stock Trigger
            p5.setReorderPoint(5);
            p5.setSupplier(s2);

            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

            log.info("Database successfully initialized with 5 products and 2 suppliers.");
        } else {
            log.info("Database already contains data. Skipping initialization.");
        }
    }
}
