package com.licorerajm.backend.repository;

import com.licorerajm.backend.entity.SaleDetailLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleDetailLotRepository extends JpaRepository<SaleDetailLot, Long> {

    List<SaleDetailLot> findBySaleDetailId(Long saleDetailId);
}