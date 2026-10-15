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
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.model.Product;
import com.kanemart.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // ===============================
    // CONSTRUCTOR
    // ===============================

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ===============================
    // GET ALL PRODUCTS
    // GET /api/products
    // ===============================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        List<Product> products = productService.getAllProducts();

        return ResponseEntity.ok(products);
    }

    // ===============================
    // GET ACTIVE PRODUCTS
    // GET /api/products/active
    // ===============================

    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {

        List<Product> products = productService.getActiveProducts();

        return ResponseEntity.ok(products);
    }

    // ===============================
    // GET PRODUCT BY ID
    // GET /api/products/{id}
    // ===============================

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        Product product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }

    // ===============================
    // GET PRODUCT BY SKU
    // GET /api/products/sku/{sku}
    // ===============================

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(
            @PathVariable String sku) {

        Product product = productService.getProductBySku(sku);

        return ResponseEntity.ok(product);
    }

    // ===============================
    // GET LOW STOCK PRODUCTS
    // GET /api/products/low-stock
    // ===============================

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {

        List<Product> products = productService.getLowStockProducts();

        return ResponseEntity.ok(products);
    }

    // ===============================
    // ADD PRODUCT
    // POST /api/products
    // ===============================

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @RequestBody Product product) {

        Product savedProduct = productService.addProduct(product);

        return ResponseEntity.ok(savedProduct);
    }

    // ===============================
    // UPDATE PRODUCT
    // PUT /api/products/{id}
    // ===============================

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        Product updatedProduct =
                productService.updateProduct(id, product);

        return ResponseEntity.ok(updatedProduct);
    }

    // ===============================
    // DELETE PRODUCT
    // DELETE /api/products/{id}
    // ===============================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }
}