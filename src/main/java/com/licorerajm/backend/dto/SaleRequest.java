package com.licorerajm.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Valid
    private List<SalePaymentRequest> payments = new ArrayList<>();

    @Size(max = 150, message = "El nombre del cliente no puede superar 150 caracteres")
    private String customerName;

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}