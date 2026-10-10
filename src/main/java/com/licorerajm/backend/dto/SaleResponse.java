package com.licorerajm.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private String customerName;
    private BigDecimal paidAmount;
    private BigDecimal creditBalance;

    private String status;

    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private Long cancelledByUserId;
    private String cancelledByUsername;

    private String creditStatus;
    private List<CreditPaymentResponse> creditPayments = new ArrayList<>();
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

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Long getCancelledByUserId() {
        return cancelledByUserId;
    }

    public void setCancelledByUserId(Long cancelledByUserId) {
        this.cancelledByUserId = cancelledByUserId;
    }

    public String getCancelledByUsername() {
        return cancelledByUsername;
    }

    public void setCancelledByUsername(String cancelledByUsername) {
        this.cancelledByUsername = cancelledByUsername;
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

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public BigDecimal getPaidAmount() { return paidAmount; }

    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getCreditBalance() { return creditBalance; }

    public void setCreditBalance(BigDecimal creditBalance) { this.creditBalance = creditBalance; }

    public String getCreditStatus() { return creditStatus; }

    public void setCreditStatus(String creditStatus) { this.creditStatus = creditStatus; }

    public List<CreditPaymentResponse> getCreditPayments() { return creditPayments; }

    public void setCreditPayments(List<CreditPaymentResponse> creditPayments) { this.creditPayments = creditPayments; }
}