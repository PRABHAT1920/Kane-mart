package com.kanemart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // ===============================
    // LOGIN
    // ===============================

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ===============================
    // OTP
    // ===============================

    @GetMapping("/otp")
    public String otp() {
        return "otp";
    }

    // ===============================
    // DASHBOARD
    // ===============================

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // ===============================
    // PRODUCTS
    // ===============================

    @GetMapping("/products")
    public String products() {
        return "products";
    }

    // ===============================
    // EMPLOYEES
    // ===============================

    @GetMapping("/employees")
    public String employees() {
        return "employees";
    }

    // ===============================
    // INVENTORY
    // ===============================

    @GetMapping("/inventory")
    public String inventory() {
        return "inventory";
    }

    // ===============================
    // LOCATIONS
    // ===============================

    @GetMapping("/locations")
    public String locations() {
        return "locations";
    }

    // ===============================
    // WAREHOUSES
    // ===============================

    @GetMapping("/warehouses")
    public String warehouses() {
        return "warehouses";
    }

    // ===============================
    // SUPPLIERS
    // ===============================

    @GetMapping("/suppliers")
    public String suppliers() {
        return "suppliers";
    }

    // ===============================
    // PURCHASE ORDERS
    // ===============================

    @GetMapping("/purchase-orders")
    public String purchaseOrders() {
        return "purchase-orders";
    }

    // ===============================
    // STOCK
    // ===============================

    @GetMapping("/stock")
    public String stock() {
        return "stock";
    }

    // ===============================
    // CUSTOMER ORDERS
    // ===============================

    @GetMapping("/orders")
    public String orders() {
        return "customer-orders";
    }

    @GetMapping("/order")
    public String order() {
        return "customer-orders";
    }
    
}