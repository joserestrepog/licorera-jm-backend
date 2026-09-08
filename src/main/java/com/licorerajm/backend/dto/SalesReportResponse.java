package com.licorerajm.backend.dto;

import java.math.BigDecimal;

public class SalesReportResponse {

    private Long saleCount;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private BigDecimal totalCost;
    private BigDecimal profit;

    public SalesReportResponse() {
    }

    public SalesReportResponse(
            Long saleCount,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal total,
            BigDecimal totalCost,
            BigDecimal profit
    ) {
        this.saleCount = saleCount;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.totalCost = totalCost;
        this.profit = profit;
    }

    public Long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Long saleCount) {
        this.saleCount = saleCount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}