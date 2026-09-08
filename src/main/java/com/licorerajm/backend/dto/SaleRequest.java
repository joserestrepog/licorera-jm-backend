package com.licorerajm.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class SaleRequest {

    @NotNull(message = "La caja es obligatoria")
    private Long cashRegisterId;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid
    private List<SaleItemRequest> items;

    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    private BigDecimal discount = BigDecimal.ZERO;

    @NotEmpty(message = "La venta debe tener al menos un método de pago")
    @Valid
    private List<SalePaymentRequest> payments;

    public Long getCashRegisterId() {
        return cashRegisterId;
    }

    public void setCashRegisterId(Long cashRegisterId) {
        this.cashRegisterId = cashRegisterId;
    }

    public List<SaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public List<SalePaymentRequest> getPayments() {
        return payments;
    }

    public void setPayments(List<SalePaymentRequest> payments) {
        this.payments = payments;
    }
}