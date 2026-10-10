package com.licorerajm.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CollectionsByDayProjection {

    LocalDate getCollectionDate();

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

