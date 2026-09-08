package com.licorerajm.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SaleNumberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextSaleNumber() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('sale_number_seq')")
                .getSingleResult())
                .longValue();
    }
}