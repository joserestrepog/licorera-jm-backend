package com.licorerajm.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryEntryResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String barcode;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime entryDate;
    private Long userId;
    private String username;
    private String notes;

    public InventoryEntryResponse(
            Long id,
            Long productId,
            String productName,
            String barcode,
            Integer quantity,
            BigDecimal purchasePrice,
            LocalDateTime entryDate,
            Long userId,
            String username,
            String notes
    ) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.entryDate = entryDate;
        this.userId = userId;
        this.username = username;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getNotes() {
        return notes;
    }
}