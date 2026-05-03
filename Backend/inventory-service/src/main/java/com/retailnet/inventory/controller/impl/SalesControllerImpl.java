package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.SalesController;
import com.retailnet.inventory.dto.SaleDTO;
import com.retailnet.inventory.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SalesControllerImpl implements SalesController {

    private final SaleService saleService;

    @Override
    public String makeSale(@RequestParam Long productId, @RequestParam Integer quantity) {
        saleService.recordSale(productId, quantity);
        return "Sale successful! Stock reduced.";
    }

    @Override
    public List<SaleDTO> getSalesHistory() {
        return saleService.getAllSales();
    }

}