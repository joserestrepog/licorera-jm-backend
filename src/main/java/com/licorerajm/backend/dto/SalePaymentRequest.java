package com.licorerajm.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SalePaymentRequest {

    @NotNull(message = "El método de pago es obligatorio")
    private Long paymentMethodId;

    @NotNull(message = "El monto del pago es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto del pago debe ser mayor que cero")
    private BigDecimal amount;

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}