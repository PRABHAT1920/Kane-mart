package com.kanemart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanemart.model.Inventory;
import com.kanemart.model.Location;
import com.kanemart.model.Order;
import com.kanemart.model.OrderItem;
import com.kanemart.model.Product;
import com.kanemart.repository.InventoryRepository;
import com.kanemart.repository.LocationRepository;
import com.kanemart.repository.OrderItemRepository;
import com.kanemart.repository.OrderRepository;
import com.kanemart.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final LocationRepository locationRepository;
    private final StockTransactionService stockTransactionService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            LocationRepository locationRepository,
            StockTransactionService stockTransactionService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.locationRepository = locationRepository;
        this.stockTransactionService = stockTransactionService;
    }

    // ==========================================
    // CREATE ORDER
    // ==========================================

    @Transactional
    public Order createOrder(
            String customerName,
            String customerMobile,
            Long productId,
            int quantity) {

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new RuntimeException(
                    "Customer name is required");
        }

        if (customerMobile == null ||
                customerMobile.trim().isEmpty()) {

            throw new RuntimeException(
                    "Customer mobile is required");
        }

        if (productId == null) {
            throw new RuntimeException(
                    "Product ID is required");
        }

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        // ------------------------------------------
        // FIND PRODUCT
        // ------------------------------------------

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"));

        if (!product.isActive()) {
            throw new RuntimeException(
                    "Product is inactive");
        }

        // ------------------------------------------
        // FIND INVENTORY
        // ------------------------------------------

        List<Inventory> inventories =
                inventoryRepository.findByProduct(product);

        Inventory selectedInventory = inventories
                .stream()
                .filter(inventory ->
                        inventory.getQuantity() >= quantity)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Insufficient inventory stock"));

        Location location =
                selectedInventory.getLocation();

        if (location == null) {
            throw new RuntimeException(
                    "Product inventory location not found");
        }

        // ------------------------------------------
        // CALCULATE TOTAL
        // ------------------------------------------

        double totalAmount =
                product.getPrice() * quantity;

        // ------------------------------------------
        // GENERATE ORDER DETAILS
        // ------------------------------------------

        String orderNumber =
                generateOrderNumber();

        String deliveryOtp =
                generateDeliveryOtp();

        // ------------------------------------------
        // CREATE ORDER
        // ------------------------------------------

        Order order = new Order(
                orderNumber,
                customerName,
                customerMobile,
                totalAmount,
                "PENDING",
                LocalDateTime.now(),
                deliveryOtp,
                false
        );

        Order savedOrder =
                orderRepository.save(order);

        // ------------------------------------------
        // CREATE ORDER ITEM
        // ------------------------------------------

        OrderItem orderItem = new OrderItem(
                savedOrder,
                product,
                quantity,
                product.getPrice()
        );

        orderItemRepository.save(orderItem);

        // ------------------------------------------
        // DEDUCT INVENTORY
        // ------------------------------------------

        stockTransactionService.stockOut(
                productId,
                location.getId(),
                quantity,
                orderNumber
        );

        return savedOrder;
    }

    // ==========================================
    // GET ALL ORDERS
    // ==========================================

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    // ==========================================
    // GET ORDER BY ID
    // ==========================================

    public Order getOrderById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"));
    }

    // ==========================================
    // GET ORDER BY NUMBER
    // ==========================================

    public Order getOrderByNumber(
            String orderNumber) {

        return orderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"));
    }

    // ==========================================
    // GET ORDERS BY STATUS
    // ==========================================

    public List<Order> getOrdersByStatus(
            String status) {

        return orderRepository.findByStatus(status);
    }

    // ==========================================
    // UPDATE ORDER STATUS
    // ==========================================

    public Order updateOrderStatus(
            Long id,
            String status) {

        Order order =
                getOrderById(id);

        order.setStatus(status);

        return orderRepository.save(order);
    }

    // ==========================================
    // VERIFY DELIVERY OTP
    // ==========================================

    public boolean verifyDeliveryOtp(
            Long orderId,
            String otp) {

        Order order =
                getOrderById(orderId);

        if (order.isDeliveryVerified()) {
            return false;
        }

        if (order.getDeliveryOtp() == null) {
            return false;
        }

        if (!order.getDeliveryOtp().equals(otp)) {
            return false;
        }

        order.setDeliveryVerified(true);

        order.setStatus("DELIVERED");

        orderRepository.save(order);

        return true;
    }

    // ==========================================
    // GENERATE ORDER NUMBER
    // ==========================================

    private String generateOrderNumber() {

        return "KM-" +
                System.currentTimeMillis();
    }

    // ==========================================
    // GENERATE DELIVERY OTP
    // ==========================================

    private String generateDeliveryOtp() {

        return String.format(
                "%06d",
                new Random().nextInt(1_000_000)
        );
    }
}