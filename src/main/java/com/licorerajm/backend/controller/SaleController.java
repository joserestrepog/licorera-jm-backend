package com.licorerajm.backend.controller;

import com.licorerajm.backend.dto.SaleCancelRequest;
import com.licorerajm.backend.dto.SaleRequest;
import com.licorerajm.backend.dto.SaleResponse;
import com.licorerajm.backend.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse create(@Valid @RequestBody SaleRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping("/{id}")
    public SaleResponse findById(@PathVariable Long id) {
        return saleService.findById(id);
    }

    @GetMapping
    public List<SaleResponse> findAll() {
        return saleService.findAll();
    }

    @PutMapping("/{id}/cancel")
    public SaleResponse cancel(
            @PathVariable Long id,
            @Valid @RequestBody SaleCancelRequest request) {

        return saleService.cancelSale(id, request);
    }

}