package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.InventoryReportResponse;
import com.licorerajm.backend.dto.SalesReportResponse;
import com.licorerajm.backend.repository.InventoryStockProjection;
import com.licorerajm.backend.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    public ReportService(
            SaleRepository saleRepository,
            ProductRepository productRepository
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
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

    public List<InventoryReportResponse> getInventoryStock() {

        return productRepository.findInventoryStock()
                .stream()
                .map(this::mapInventoryStock)
                .toList();
    }

    private InventoryReportResponse mapInventoryStock(
            InventoryStockProjection projection
    ) {

        return new InventoryReportResponse(
                projection.getProductId(),
                projection.getProductName(),
                projection.getBarcode(),
                projection.getCategoryName(),
                projection.getCurrentStock(),
                projection.getMinimumStock(),
                projection.getPurchasePrice(),
                projection.getStockValue()
        );
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