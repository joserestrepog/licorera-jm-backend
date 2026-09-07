package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.InventoryLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLotRepository extends JpaRepository<InventoryLot, Long> {
}