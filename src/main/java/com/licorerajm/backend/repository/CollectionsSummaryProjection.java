package com.licorerajm.backend.repository;

import java.math.BigDecimal;

public interface CollectionsSummaryProjection {

    BigDecimal getSalePaymentsTotal();

    BigDecimal getSalePaymentsCash();

    BigDecimal getSalePaymentsTransfer();

    BigDecimal getCreditPaymentsTotal();

    BigDecimal getCreditPaymentsCash();

    BigDecimal getCreditPaymentsTransfer();

    BigDecimal getTotalCollected();

    BigDecimal getCashCollected();

    BigDecimal getTransferCollected();
}
