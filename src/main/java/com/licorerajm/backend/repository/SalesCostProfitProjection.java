package com.licorerajm.backend.repository;

import java.math.BigDecimal;

public interface SalesCostProfitProjection {

    BigDecimal getTotalCost();

    BigDecimal getProfit();
}