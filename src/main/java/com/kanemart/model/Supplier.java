package com.kanemart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private boolean active = true;

    /*
     * Average supplier delivery time in days.
     *
     * This value will eventually be calculated
     * automatically from SupplierDelivery records.
     */
    @Column(nullable = false)
    private double averageLeadTime = 0.0;


    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public Supplier() {
    }


    public Supplier(
            String name,
            String contact,
            String email,
            String address) {

        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.active = true;
        this.averageLeadTime = 0.0;
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getContact() {
        return contact;
    }


    public void setContact(String contact) {
        this.contact = contact;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    public double getAverageLeadTime() {
        return averageLeadTime;
    }


    public void setAverageLeadTime(double averageLeadTime) {
        this.averageLeadTime = averageLeadTime;
    }
}