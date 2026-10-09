package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.InventoryLot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryLotRepository extends JpaRepository<InventoryLot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<InventoryLot> findByProductIdAndAvailableQuantityGreaterThanAndActiveTrueOrderByEntryDateAscIdAsc(
            Long productId,
            Integer availableQuantity
    );

    @Modifying
    @Query("""
        UPDATE InventoryLot l
        SET l.active = false,
            l.availableQuantity = 0,
            l.invalidatedAt = CURRENT_TIMESTAMP
        WHERE l.product.id = :productId
        """)
    void invalidateByProductId(@Param("productId") Long productId);
}