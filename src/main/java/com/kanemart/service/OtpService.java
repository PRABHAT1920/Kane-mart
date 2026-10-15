package com.kanemart.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.kanemart.model.OtpRecord;
import com.kanemart.repository.OtpRecordRepository;

@Service
public class OtpService {

    private final OtpRecordRepository otpRecordRepository;

    public OtpService(OtpRecordRepository otpRecordRepository) {
        this.otpRecordRepository = otpRecordRepository;
    }

    public String generateOtp(String mobile) {

        // Generate 6-digit OTP
        String otp = String.format(
                "%06d",
                new Random().nextInt(1_000_000)
        );

        // OTP valid for 5 minutes
        LocalDateTime expiryTime =
                LocalDateTime.now().plusMinutes(5);

        // Save OTP to database
        OtpRecord otpRecord =
                new OtpRecord(mobile, otp, expiryTime);

        otpRecordRepository.save(otpRecord);

        // Show OTP only in VS Code terminal
        System.out.println();
        System.out.println("=================================");
        System.out.println("       KaneMart OTP");
        System.out.println("=================================");
        System.out.println("Mobile: " + mobile);
        System.out.println("OTP: " + otp);
        System.out.println("Expires: " + expiryTime);
        System.out.println("=================================");
        System.out.println();

        return otp;
    }

    public boolean verifyOtp(String mobile, String otp) {

        OtpRecord record =
                otpRecordRepository
                        .findTopByMobileOrderByIdDesc(mobile)
                        .orElse(null);

        if (record == null) {
            return false;
        }

        // Already used
        if (record.isVerified()) {
            return false;
        }

        // Expired
        if (LocalDateTime.now().isAfter(record.getExpiryTime())) {
            return false;
        }

        // Incorrect OTP
        if (!record.getOtp().equals(otp)) {
            return false;
        }

        // Mark as verified
        record.setVerified(true);
        otpRecordRepository.save(record);

        return true;
    }
}