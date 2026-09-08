package com.licorerajm.backend.dto;

import java.math.BigDecimal;

public class InventoryReportResponse {

    private Long productId;
    private String productName;
    private String barcode;
    private String categoryName;
    private Integer currentStock;
    private Integer minimumStock;
    private BigDecimal purchasePrice;
    private BigDecimal stockValue;

    public InventoryReportResponse() {
    }

    public InventoryReportResponse(
            Long productId,
            String productName,
            String barcode,
            String categoryName,
            Integer currentStock,
            Integer minimumStock,
            BigDecimal purchasePrice,
            BigDecimal stockValue
    ) {
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.categoryName = categoryName;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.purchasePrice = purchasePrice;
        this.stockValue = stockValue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = stockValue;
    }
}