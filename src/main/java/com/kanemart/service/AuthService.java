package com.kanemart.service;

import org.springframework.stereotype.Service;

import com.kanemart.model.Employee;
import com.kanemart.repository.EmployeeRepository;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final OtpService otpService;

    public AuthService(
            EmployeeRepository employeeRepository,
            OtpService otpService) {

        this.employeeRepository = employeeRepository;
        this.otpService = otpService;
    }

    // ==========================================
    // SEND OTP
    // ==========================================

    public String sendOtp(String mobile) {

        Employee employee = employeeRepository
                .findByMobile(mobile)
                .orElse(null);

        if (employee == null) {
            return null;
        }

        if (!employee.isActive()) {
            return null;
        }

        // Generate OTP
        String otp = otpService.generateOtp(mobile);

        return otp;
    }

    // ==========================================
    // VERIFY OTP
    // ==========================================

    public Employee verifyOtp(
            String mobile,
            String otp) {

        Employee employee = employeeRepository
                .findByMobile(mobile)
                .orElse(null);

        if (employee == null) {
            return null;
        }

        if (!employee.isActive()) {
            return null;
        }

        boolean verified =
                otpService.verifyOtp(
                        mobile,
                        otp
                );

        if (!verified) {
            return null;
        }

        return employee;
    }
}