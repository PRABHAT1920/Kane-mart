package com.kanemart.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kanemart.model.Order;
import com.kanemart.repository.OrderRepository;

@Controller
public class DeliveryVerificationController {

    private final OrderRepository orderRepository;

    // Constructor injection
    public DeliveryVerificationController(
            OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // ==========================================
    // 1. OPEN DELIVERY VERIFICATION WEBSITE
    // ==========================================
    @GetMapping("/delivery-verification")
    public String deliveryVerificationPage() {
        return "delivery-verification";
    }

    // ==========================================
    // 2. GET ORDER STATUS
    // ==========================================
    @GetMapping("/api/delivery/{orderId}/status")
    @ResponseBody
    public ResponseEntity<?> getOrderStatus(
            @PathVariable Long orderId) {

        Optional<Order> result =
                orderRepository.findById(orderId);

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Order not found."
                    ));
        }

        Order order = result.get();

        return ResponseEntity.ok(
                Map.of(
                        "orderId", order.getId(),
                        "status", String.valueOf(order.getStatus())
                )
        );
    }

    // ==========================================
    // 3. VERIFY DELIVERY OTP
    // ==========================================
    @PostMapping("/api/delivery/{orderId}/verify")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> verifyDeliveryOtp(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        // Validate the submitted OTP
        String submittedOtp =
                request == null ? null : request.get("otp");

        if (submittedOtp == null
                || !submittedOtp.matches("\\d{4,8}")) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Enter a valid 4-8 digit OTP."
                    ));
        }

        // Find the order
        Optional<Order> result =
                orderRepository.findById(orderId);

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Order not found."
                    ));
        }

        Order order = result.get();

        String currentStatus =
                String.valueOf(order.getStatus());

        // Prevent duplicate delivery completion
        if ("DELIVERED".equalsIgnoreCase(currentStatus)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "This order has already been delivered.",
                            "status", currentStatus
                    ));
        }

        // Only dispatched orders can be delivered
        if (!"DISPATCHED".equalsIgnoreCase(currentStatus)
                && !"OUT_FOR_DELIVERY".equalsIgnoreCase(currentStatus)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "The order must be dispatched before delivery verification.",
                            "status", currentStatus
                    ));
        }

        // Read the OTP stored for this order.
        // Adapt this getter if your Order class uses another name.
        String expectedOtp = order.getDeliveryOtp();

        // Verify the OTP on the backend
        if (!constantTimeEquals(expectedOtp, submittedOtp)) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Incorrect OTP. Order status was not changed.",
                            "status", currentStatus
                    ));
        }

        // Correct OTP: mark order as delivered.
        // If status is an enum, replace this with the enum constant.
        order.setStatus("DELIVERED");

        orderRepository.save(order);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP verified successfully. Order delivered!",
                        "orderId", order.getId(),
                        "status", "DELIVERED"
                )
        );
    }

    // ==========================================
    // 4. COMPARE OTP VALUES
    // ==========================================
    private boolean constantTimeEquals(
            String expected,
            String actual) {

        if (expected == null || actual == null
                || expected.length() != actual.length()) {
            return false;
        }

        int difference = 0;

        for (int i = 0; i < expected.length(); i++) {
            difference |=
                    expected.charAt(i) ^ actual.charAt(i);
        }

        return difference == 0;
    }
}