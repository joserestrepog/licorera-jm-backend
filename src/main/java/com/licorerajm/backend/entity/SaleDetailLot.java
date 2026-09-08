package com.licorerajm.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "sale_detail_lot",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_sale_detail_lot",
                        columnNames = {"sale_detail_id", "inventory_lot_id"}
                )
        }
)
public class SaleDetailLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_detail_id", nullable = false)
    private SaleDetail saleDetail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_lot_id", nullable = false)
    private InventoryLot inventoryLot;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost;

    public Long getId() {
        return id;
    }

    public SaleDetail getSaleDetail() {
        return saleDetail;
    }

    public void setSaleDetail(SaleDetail saleDetail) {
        this.saleDetail = saleDetail;
    }

    public InventoryLot getInventoryLot() {
        return inventoryLot;
    }

    public void setInventoryLot(InventoryLot inventoryLot) {
        this.inventoryLot = inventoryLot;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}