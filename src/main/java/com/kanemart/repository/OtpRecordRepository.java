package com.kanemart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.OtpRecord;

public interface OtpRecordRepository extends JpaRepository<OtpRecord, Long> {

    Optional<OtpRecord> findTopByMobileOrderByIdDesc(String mobile);

}