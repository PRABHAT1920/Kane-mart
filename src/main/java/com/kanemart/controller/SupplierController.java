package com.kanemart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.model.Supplier;
import com.kanemart.service.SupplierService;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(
            SupplierService supplierService) {

        this.supplierService = supplierService;
    }


    // ==========================================
    // CREATE SUPPLIER
    // POST /api/suppliers
    // ==========================================

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(
            @RequestBody Supplier supplier) {

        return ResponseEntity.ok(
                supplierService.createSupplier(
                        supplier
                )
        );
    }


    // ==========================================
    // GET ALL SUPPLIERS
    // GET /api/suppliers
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Supplier>>
            getAllSuppliers() {

        return ResponseEntity.ok(
                supplierService.getAllSuppliers()
        );
    }


    // ==========================================
    // GET ACTIVE SUPPLIERS
    // GET /api/suppliers/active
    // ==========================================

    @GetMapping("/active")
    public ResponseEntity<List<Supplier>>
            getActiveSuppliers() {

        return ResponseEntity.ok(
                supplierService.getActiveSuppliers()
        );
    }


    // ==========================================
    // GET SUPPLIER BY ID
    // GET /api/suppliers/{id}
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Supplier>
            getSupplierById(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                supplierService.getSupplierById(id)
        );
    }


    // ==========================================
    // GET SUPPLIER BY EMAIL
    // GET /api/suppliers/email/{email}
    // ==========================================

    @GetMapping("/email/{email}")
    public ResponseEntity<Supplier>
            getSupplierByEmail(
                    @PathVariable String email) {

        return ResponseEntity.ok(
                supplierService.getSupplierByEmail(
                        email
                )
        );
    }


    // ==========================================
    // SEARCH SUPPLIERS
    // GET /api/suppliers/search?name=ABC
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<List<Supplier>>
            searchSuppliers(
                    @RequestParam String name) {

        return ResponseEntity.ok(
                supplierService.searchSuppliers(name)
        );
    }


    // ==========================================
    // UPDATE SUPPLIER
    // PUT /api/suppliers/{id}
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<Supplier>
            updateSupplier(
                    @PathVariable Long id,
                    @RequestBody Supplier supplier) {

        return ResponseEntity.ok(
                supplierService.updateSupplier(
                        id,
                        supplier
                )
        );
    }


    // ==========================================
    // DEACTIVATE SUPPLIER
    // DELETE /api/suppliers/{id}
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
            deactivateSupplier(
                    @PathVariable Long id) {

        supplierService.deactivateSupplier(id);

        return ResponseEntity.ok(
                "Supplier deactivated successfully"
        );
    }
}