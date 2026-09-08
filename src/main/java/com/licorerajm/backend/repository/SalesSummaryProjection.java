package com.licorerajm.backend.repository;

import java.math.BigDecimal;

public interface SalesSummaryProjection {

    Long getSaleCount();

    BigDecimal getSubtotal();

    BigDecimal getDiscount();

    BigDecimal getTotal();

    BigDecimal getTotalCost();

    BigDecimal getProfit();
}