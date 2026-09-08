package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findAllByOrderBySaleDateDesc();

    @Query(value = """
            SELECT
                COUNT(*) AS saleCount,
                COALESCE(SUM(s.subtotal), 0) AS subtotal,
                COALESCE(SUM(s.discount), 0) AS discount,
                COALESCE(SUM(s.total), 0) AS total
            FROM sale s
            WHERE s.status = 'COMPLETED'
              AND s.sale_date >= :start
              AND s.sale_date < :end
            """, nativeQuery = true)
    SalesSummaryProjection findSalesSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
            SELECT
                COALESCE(SUM(sd.total_cost), 0) AS totalCost,
                COALESCE(SUM(sd.profit), 0) AS profit
            FROM sale_detail sd
            INNER JOIN sale s ON s.id = sd.sale_id
            WHERE s.status = 'COMPLETED'
              AND s.sale_date >= :start
              AND s.sale_date < :end
            """, nativeQuery = true)
    SalesCostProfitProjection findSalesCostAndProfit(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
            SELECT
                p.id AS productId,
                p.name AS productName,
                p.barcode AS barcode,
                SUM(sd.quantity) AS quantitySold,
                COALESCE(SUM(sd.total), 0) AS totalSales,
                COALESCE(SUM(sd.total_cost), 0) AS totalCost,
                COALESCE(SUM(sd.profit), 0) AS profit
            FROM sale_detail sd
            INNER JOIN sale s ON s.id = sd.sale_id
            INNER JOIN product p ON p.id = sd.product_id
            WHERE s.status = 'COMPLETED'
              AND s.sale_date >= :start
              AND s.sale_date < :end
            GROUP BY p.id, p.name, p.barcode
            ORDER BY quantitySold DESC
            """, nativeQuery = true)
    List<SalesByProductProjection> findSalesByProduct(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}