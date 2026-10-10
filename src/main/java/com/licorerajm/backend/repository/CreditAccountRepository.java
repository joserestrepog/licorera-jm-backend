package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface CreditAccountRepository
        extends JpaRepository<CreditAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CreditAccount c WHERE c.id = :id")
    Optional<CreditAccount> findWithLockById(@Param("id") Long id);

    Optional<CreditAccount> findBySaleId(Long saleId);

    List<CreditAccount> findByStatusOrderByCreatedAtDesc(String status);

    List<CreditAccount> findAllByOrderByCreatedAtDesc();

    List<CreditAccount> findByCustomerNameContainingIgnoreCaseOrderByCreatedAtDesc(
            String customerName
    );
}