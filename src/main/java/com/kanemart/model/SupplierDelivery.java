package com.kanemart.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "supplier_deliveries")
public class SupplierDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ==========================================
    // SUPPLIER
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;


    // ==========================================
    // PURCHASE ORDER NUMBER
    // ==========================================

    @Column(nullable = false)
    private String purchaseOrderNumber;


    // ==========================================
    // ORDER DATE
    // ==========================================

    @Column(nullable = false)
    private LocalDateTime orderDate;


    // ==========================================
    // DELIVERY DATE
    // ==========================================

    @Column(nullable = false)
    private LocalDateTime deliveryDate;


    // ==========================================
    // DELIVERY QUANTITY
    // ==========================================

    @Column(nullable = false)
    private int quantity;


    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public SupplierDelivery() {
    }


    public SupplierDelivery(
            Supplier supplier,
            String purchaseOrderNumber,
            LocalDateTime orderDate,
            LocalDateTime deliveryDate,
            int quantity) {

        this.supplier = supplier;
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.quantity = quantity;
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }


    public Supplier getSupplier() {
        return supplier;
    }


    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }


    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }


    public void setPurchaseOrderNumber(
            String purchaseOrderNumber) {

        this.purchaseOrderNumber =
                purchaseOrderNumber;
    }


    public LocalDateTime getOrderDate() {
        return orderDate;
    }


    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }


    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }


    public void setDeliveryDate(
            LocalDateTime deliveryDate) {

        this.deliveryDate = deliveryDate;
    }


    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    // ==========================================
    // LEAD TIME
    // ==========================================

    @Transient
    public long getLeadTimeDays() {

        if (orderDate == null ||
            deliveryDate == null) {

            return 0;
        }

        return java.time.Duration
                .between(
                        orderDate,
                        deliveryDate
                )
                .toDays();
    }
}