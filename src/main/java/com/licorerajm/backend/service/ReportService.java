package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.SalesReportResponse;
import com.licorerajm.backend.repository.SaleRepository;
import com.licorerajm.backend.repository.SalesByProductProjection;
import com.licorerajm.backend.repository.SalesCostProfitProjection;
import com.licorerajm.backend.repository.SalesSummaryProjection;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final SaleRepository saleRepository;

    public ReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public SalesReportResponse getSalesSummary(
            LocalDate from,
            LocalDate to
    ) {

        validateDateRange(from, to);

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        SalesSummaryProjection summary =
                saleRepository.findSalesSummary(start, end);

        SalesCostProfitProjection costProfit =
                saleRepository.findSalesCostAndProfit(start, end);

        return new SalesReportResponse(
                summary.getSaleCount(),
                summary.getSubtotal(),
                summary.getDiscount(),
                summary.getTotal(),
                costProfit.getTotalCost(),
                costProfit.getProfit()
        );
    }

    public List<SalesByProductProjection> getSalesByProduct(
            LocalDate from,
            LocalDate to
    ) {

        validateDateRange(from, to);

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        return saleRepository.findSalesByProduct(start, end);
    }

    private void validateDateRange(
            LocalDate from,
            LocalDate to
    ) {

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Las fechas de inicio y fin son obligatorias"
            );
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
            );
        }
    }
}