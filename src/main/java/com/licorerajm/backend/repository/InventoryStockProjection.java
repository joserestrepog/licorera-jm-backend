package com.licorerajm.backend.repository;

import java.math.BigDecimal;

public interface InventoryStockProjection {

    Long getProductId();

    String getProductName();

    String getBarcode();

    String getCategoryName();

    Integer getCurrentStock();

    Integer getMinimumStock();

    BigDecimal getPurchasePrice();

    BigDecimal getStockValue();
}