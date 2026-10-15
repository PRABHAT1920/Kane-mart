package com.kanemart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.dto.CreateOrderRequest;
import com.kanemart.model.Order;
import com.kanemart.model.OrderItem;
import com.kanemart.repository.OrderItemRepository;
import com.kanemart.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public OrderController(
            OrderService orderService,
            OrderItemRepository orderItemRepository) {

        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    // ==========================================
    // CREATE ORDER - JSON REQUEST
    // ==========================================

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody CreateOrderRequest request) {

        Order order = orderService.createOrder(
                request.getCustomerName(),
                request.getCustomerMobile(),
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(order);
    }

    // ==========================================
    // CREATE ORDER - REQUEST PARAMETERS
    // ==========================================
    /*
     * Kept as a separate endpoint so existing API
     * calls using request parameters continue to work.
     *
     * Example:
     *
     * POST /api/orders/create
     *     ?customerName=gundur
     *     &customerMobile=9900768905
     *     &productId=1
     *     &quantity=1
     */

    @PostMapping("/create")
    public ResponseEntity<Order> createOrderWithParameters(
            @RequestParam String customerName,
            @RequestParam String customerMobile,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        Order order = orderService.createOrder(
                customerName,
                customerMobile,
                productId,
                quantity
        );

        return ResponseEntity.ok(order);
    }

    // ==========================================
    // GET ALL ORDERS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // ==========================================
    // GET ORDER BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // ==========================================
    // GET ORDER BY NUMBER
    // ==========================================

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(
                orderService.getOrderByNumber(orderNumber)
        );
    }

    // ==========================================
    // GET ORDERS BY STATUS
    // ==========================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                orderService.getOrdersByStatus(status)
        );
    }

    // ==========================================
    // GET ORDER ITEMS
    // ==========================================

    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(
            @PathVariable Long id) {

        Order order =
                orderService.getOrderById(id);

        return ResponseEntity.ok(
                orderItemRepository.findByOrder(order)
        );
    }

    // ==========================================
    // UPDATE ORDER STATUS
    // ==========================================

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        status
                )
        );
    }

    // ==========================================
    // DELIVERY OTP VERIFICATION
    // ==========================================

    @PostMapping("/{id}/verify-delivery")
    public ResponseEntity<?> verifyDeliveryOtp(
            @PathVariable Long id,
            @RequestParam String otp) {

        boolean verified =
                orderService.verifyDeliveryOtp(
                        id,
                        otp
                );

        if (!verified) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Invalid delivery OTP"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Delivery verified successfully"
                )
        );
    }
}