package com.licorerajm.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SalesByDayProjection {

    LocalDate getSaleDate();

    Long getSaleCount();

    BigDecimal getSubtotal();

    BigDecimal getDiscount();

    BigDecimal getTotal();

    BigDecimal getTotalCost();

    BigDecimal getProfit();
}