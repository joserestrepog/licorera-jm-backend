package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.InventoryLot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface InventoryLotRepository extends JpaRepository<InventoryLot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<InventoryLot> findByProductIdAndAvailableQuantityGreaterThanAndActiveTrueOrderByEntryDateAscIdAsc(
            Long productId,
            Integer availableQuantity
    );
}