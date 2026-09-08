package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.CashRegister;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    Optional<CashRegister> findByUserIdAndStatus(Long userId, String status);

    boolean existsByUserIdAndStatus(Long userId, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CashRegister> findWithLockById(Long id);
}