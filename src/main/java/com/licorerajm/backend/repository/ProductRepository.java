package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByBarcode(String barcode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockById(Long id);

    @Query(value = """
            SELECT
                p.id AS productId,
                p.name AS productName,
                p.barcode AS barcode,
                c.name AS categoryName,
                p.current_stock AS currentStock,
                p.minimum_stock AS minimumStock,
                p.purchase_price AS purchasePrice,
                (p.current_stock * p.purchase_price) AS stockValue
            FROM product p
            INNER JOIN category c ON c.id = p.category_id
            WHERE p.active = true
            ORDER BY p.name ASC
            """, nativeQuery = true)
    List<InventoryStockProjection> findInventoryStock();
}