package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.InventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEntryRepository extends JpaRepository<InventoryEntry, Long> {
}