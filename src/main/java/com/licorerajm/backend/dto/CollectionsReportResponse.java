package com.licorerajm.backend.dto;


import java.math.BigDecimal;

public class CollectionsReportResponse {

    private BigDecimal salePaymentsTotal;
    private BigDecimal salePaymentsCash;
    private BigDecimal salePaymentsTransfer;

    private BigDecimal creditPaymentsTotal;
    private BigDecimal creditPaymentsCash;
    private BigDecimal creditPaymentsTransfer;

    private BigDecimal totalCollected;
    private BigDecimal cashCollected;
    private BigDecimal transferCollected;

    public CollectionsReportResponse(
            BigDecimal salePaymentsTotal,
            BigDecimal salePaymentsCash,
            BigDecimal salePaymentsTransfer,
            BigDecimal creditPaymentsTotal,
            BigDecimal creditPaymentsCash,
            BigDecimal creditPaymentsTransfer,
            BigDecimal totalCollected,
            BigDecimal cashCollected,
            BigDecimal transferCollected) {

        this.salePaymentsTotal = salePaymentsTotal;
        this.salePaymentsCash = salePaymentsCash;
        this.salePaymentsTransfer = salePaymentsTransfer;
        this.creditPaymentsTotal = creditPaymentsTotal;
        this.creditPaymentsCash = creditPaymentsCash;
        this.creditPaymentsTransfer = creditPaymentsTransfer;
        this.totalCollected = totalCollected;
        this.cashCollected = cashCollected;
        this.transferCollected = transferCollected;
    }

    public BigDecimal getSalePaymentsTotal() {
        return salePaymentsTotal;
    }

    public void setSalePaymentsTotal(BigDecimal salePaymentsTotal) {
        this.salePaymentsTotal = salePaymentsTotal;
    }

    public BigDecimal getSalePaymentsCash() {
        return salePaymentsCash;
    }

    public void setSalePaymentsCash(BigDecimal salePaymentsCash) {
        this.salePaymentsCash = salePaymentsCash;
    }

    public BigDecimal getSalePaymentsTransfer() {
        return salePaymentsTransfer;
    }

    public void setSalePaymentsTransfer(BigDecimal salePaymentsTransfer) {
        this.salePaymentsTransfer = salePaymentsTransfer;
    }

    public BigDecimal getCreditPaymentsTotal() {
        return creditPaymentsTotal;
    }

    public void setCreditPaymentsTotal(BigDecimal creditPaymentsTotal) {
        this.creditPaymentsTotal = creditPaymentsTotal;
    }

    public BigDecimal getCreditPaymentsCash() {
        return creditPaymentsCash;
    }

    public void setCreditPaymentsCash(BigDecimal creditPaymentsCash) {
        this.creditPaymentsCash = creditPaymentsCash;
    }

    public BigDecimal getCreditPaymentsTransfer() {
        return creditPaymentsTransfer;
    }

    public void setCreditPaymentsTransfer(BigDecimal creditPaymentsTransfer) {
        this.creditPaymentsTransfer = creditPaymentsTransfer;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getCashCollected() {
        return cashCollected;
    }

    public void setCashCollected(BigDecimal cashCollected) {
        this.cashCollected = cashCollected;
    }

    public BigDecimal getTransferCollected() {
        return transferCollected;
    }

    public void setTransferCollected(BigDecimal transferCollected) {
        this.transferCollected = transferCollected;
    }
}
