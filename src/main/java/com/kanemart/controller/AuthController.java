package com.kanemart.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.dto.LoginRequest;
import com.kanemart.dto.OtpRequest;
import com.kanemart.model.Employee;
import com.kanemart.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService) {

        this.authService = authService;
    }

    // ==========================================
    // SEND OTP
    // ==========================================

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
            @RequestBody LoginRequest request) {

        String mobile = request.getMobile();

        String otp =
                authService.sendOtp(mobile);

        if (otp == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Employee not found or inactive"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "OTP generated successfully",

                        // DEVELOPMENT ONLY
                        "otp", otp,

                        "mobile", mobile
                )
        );
    }

    // ==========================================
    // VERIFY OTP
    // ==========================================

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody OtpRequest request) {

        Employee employee =
                authService.verifyOtp(
                        request.getMobile(),
                        request.getOtp()
                );

        if (employee == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Invalid or expired OTP"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "OTP verified successfully",
                        "employeeId",
                        employee.getId(),
                        "employeeName",
                        employee.getName(),
                        "role",
                        employee.getRole()
                )
        );
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Logged out successfully"
                )
        );
    }
}