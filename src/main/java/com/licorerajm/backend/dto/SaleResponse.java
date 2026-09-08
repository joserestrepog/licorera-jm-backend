package com.licorerajm.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponse {

    private Long id;
    private Long saleNumber;

    private Long cashRegisterId;

    private Long userId;
    private String username;

    private LocalDateTime saleDate;

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;

    private String status;

    private String cancellationReason;

    private List<SaleDetailResponse> items;
    private List<SalePaymentResponse> payments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(Long saleNumber) {
        this.saleNumber = saleNumber;
    }

    public Long getCashRegisterId() {
        return cashRegisterId;
    }

    public void setCashRegisterId(Long cashRegisterId) {
        this.cashRegisterId = cashRegisterId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SaleDetailResponse> getItems() {
        return items;
    }

    public void setItems(List<SaleDetailResponse> items) {
        this.items = items;
    }

    public List<SalePaymentResponse> getPayments() {
        return payments;
    }

    public void setPayments(List<SalePaymentResponse> payments) {
        this.payments = payments;
    }

    public String getCancellationReason() { return cancellationReason; }

    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

}