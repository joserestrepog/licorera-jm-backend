package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByBarcode(String barcode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockById(Long id);
}