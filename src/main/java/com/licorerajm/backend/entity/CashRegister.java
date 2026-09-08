package com.licorerajm.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_register")
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "opening_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal openingAmount;

    @Column(name = "cash_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal cashSales;

    @Column(name = "transfer_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal transferSales;

    @Column(name = "total_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "expected_cash", nullable = false, precision = 12, scale = 2)
    private BigDecimal expectedCash;

    @Column(name = "counted_cash", precision = 12, scale = 2)
    private BigDecimal countedCash;

    @Column(precision = 12, scale = 2)
    private BigDecimal difference;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 255)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (openedAt == null) {
            openedAt = LocalDateTime.now();
        }

        if (openingAmount == null) {
            openingAmount = BigDecimal.ZERO;
        }

        if (cashSales == null) {
            cashSales = BigDecimal.ZERO;
        }

        if (transferSales == null) {
            transferSales = BigDecimal.ZERO;
        }

        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        if (expectedCash == null) {
            expectedCash = BigDecimal.ZERO;
        }

        if (status == null) {
            status = "OPEN";
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public BigDecimal getOpeningAmount() {
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount) {
        this.openingAmount = openingAmount;
    }

    public BigDecimal getCashSales() {
        return cashSales;
    }

    public void setCashSales(BigDecimal cashSales) {
        this.cashSales = cashSales;
    }

    public BigDecimal getTransferSales() {
        return transferSales;
    }

    public void setTransferSales(BigDecimal transferSales) {
        this.transferSales = transferSales;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(BigDecimal expectedCash) {
        this.expectedCash = expectedCash;
    }

    public BigDecimal getCountedCash() {
        return countedCash;
    }

    public void setCountedCash(BigDecimal countedCash) {
        this.countedCash = countedCash;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}