package com.licorerajm.backend.repository;

import java.math.BigDecimal;

public interface SalesByProductProjection {

    Long getProductId();

    String getProductName();

    String getBarcode();

    Long getQuantitySold();

    BigDecimal getTotalSales();

    BigDecimal getTotalCost();

    BigDecimal getProfit();
}