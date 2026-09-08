package com.licorerajm.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CashRegisterRequest {

    @NotNull(message = "El monto inicial es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto inicial no puede ser negativo")
    private BigDecimal openingAmount;

    @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
    private String notes;

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
}