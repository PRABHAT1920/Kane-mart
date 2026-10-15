package com.kanemart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kanemart.model.Product;
import com.kanemart.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ==========================================
    // ADD NEW PRODUCT
    // ==========================================

    public Product addProduct(Product product) {

        if (productRepository.existsBySku(product.getSku())) {
            throw new RuntimeException(
                    "Product with SKU already exists"
            );
        }

        return productRepository.save(product);
    }

    // ==========================================
    // GET ALL PRODUCTS
    // ==========================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // ==========================================
    // GET ACTIVE PRODUCTS
    // ==========================================

    public List<Product> getActiveProducts() {

        return productRepository.findByActiveTrue();
    }

    // ==========================================
    // GET PRODUCT BY ID
    // ==========================================

    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        )
                );
    }

    // ==========================================
    // GET PRODUCT BY SKU
    // ==========================================

    public Product getProductBySku(String sku) {

        return productRepository.findBySku(sku)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        )
                );
    }

    // ==========================================
    // UPDATE PRODUCT
    // ==========================================

    public Product updateProduct(
            Long id,
            Product updatedProduct) {

        Product existingProduct =
                getProductById(id);

        existingProduct.setName(
                updatedProduct.getName()
        );

        existingProduct.setSku(
                updatedProduct.getSku()
        );

        existingProduct.setPrice(
                updatedProduct.getPrice()
        );

        existingProduct.setQuantity(
                updatedProduct.getQuantity()
        );

        existingProduct.setReorderLevel(
                updatedProduct.getReorderLevel()
        );

        existingProduct.setActive(
                updatedProduct.isActive()
        );

        return productRepository.save(
                existingProduct
        );
    }

    // ==========================================
    // DELETE PRODUCT
    // ==========================================

    public void deleteProduct(Long id) {

        Product product =
                getProductById(id);

        productRepository.delete(product);
    }

    // ==========================================
    // GET LOW-STOCK PRODUCTS
    // ==========================================

    public List<Product> getLowStockProducts() {

        return productRepository
                .findByQuantityLessThanEqual(0);
    }

    // ==========================================
    // GET TOTAL NUMBER OF PRODUCTS
    // ==========================================

    public long getTotalProducts() {

        return productRepository.count();
    }

    // ==========================================
    // GET AVAILABLE STOCK
    // ==========================================

    public long getAvailableStock() {

        return productRepository.findAll()
                .stream()
                .filter(Product::isActive)
                .mapToLong(Product::getQuantity)
                .sum();
    }

    // ==========================================
    // GET LOW-STOCK COUNT
    // ==========================================

    public long getLowStockCount() {

        return productRepository.findAll()
                .stream()
                .filter(Product::isActive)
                .filter(product ->
                        product.getQuantity()
                                <= product.getReorderLevel()
                )
                .count();
    }
}