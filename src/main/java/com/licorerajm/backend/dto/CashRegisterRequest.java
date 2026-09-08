package com.licorerajm.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CashRegisterRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El monto de apertura es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto de apertura no puede ser negativo")
    private BigDecimal openingAmount;
    private BigDecimal countedCash;

    private String notes;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getOpeningAmount() {
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount) {
        this.openingAmount = openingAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getCountedCash() {
        return countedCash;
    }

    public void setCountedCash(BigDecimal countedCash) {
        this.countedCash = countedCash;
    }
}