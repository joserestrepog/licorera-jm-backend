package com.licorerajm.backend.controller;

import com.licorerajm.backend.dto.CreditAccountResponse;
import com.licorerajm.backend.dto.CreditPaymentRequest;
import com.licorerajm.backend.service.CreditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping
    public List<CreditAccountResponse> findOutstandingCredits() {
        return creditService.findOutstandingCredits();
    }

    @GetMapping("/{id}")
    public CreditAccountResponse findById(@PathVariable Long id) {
        return creditService.findById(id);
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditAccountResponse registerPayment(
            @PathVariable Long id,
            @Valid @RequestBody CreditPaymentRequest request
    ) {
        return creditService.registerPayment(id, request);
    }
}