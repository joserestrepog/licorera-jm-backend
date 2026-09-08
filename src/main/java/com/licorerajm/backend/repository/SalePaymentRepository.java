package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.SalePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {

    List<SalePayment> findBySaleId(Long saleId);
}