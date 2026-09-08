package com.licorerajm.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CashRegisterCloseRequest {

    @NotNull(message = "El efectivo contado es obligatorio")
    @DecimalMin(value = "0.00", message = "El efectivo contado no puede ser negativo")
    private BigDecimal countedCash;

    private String notes;

    public BigDecimal getCountedCash() {
        return countedCash;
    }

    public void setCountedCash(BigDecimal countedCash) {
        this.countedCash = countedCash;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}