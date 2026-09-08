package com.licorerajm.backend.controller;

import com.licorerajm.backend.dto.CashRegisterCloseRequest;
import com.licorerajm.backend.dto.CashRegisterRequest;
import com.licorerajm.backend.dto.CashRegisterResponse;
import com.licorerajm.backend.service.CashRegisterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-registers")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;

    public CashRegisterController(CashRegisterService cashRegisterService) {
        this.cashRegisterService = cashRegisterService;
    }

    @GetMapping
    public List<CashRegisterResponse> findAll() {
        return cashRegisterService.findAll();
    }

    @GetMapping("/{id}")
    public CashRegisterResponse findById(@PathVariable Long id) {
        return cashRegisterService.findById(id);
    }

    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    public CashRegisterResponse open(
            @Valid @RequestBody CashRegisterRequest request) {

        return cashRegisterService.openCashRegister(request);
    }

    @PostMapping("/{id}/close")
    public CashRegisterResponse close(
            @PathVariable Long id,
            @Valid @RequestBody CashRegisterCloseRequest request) {

        return cashRegisterService.closeCashRegister(id, request);
    }
}