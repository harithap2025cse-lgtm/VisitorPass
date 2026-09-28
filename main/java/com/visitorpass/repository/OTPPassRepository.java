package com.visitorpass.repository;

import com.visitorpass.entity.OTPPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface OTPPassRepository extends JpaRepository<OTPPass, Long> {

    Optional<OTPPass> findByOtp(String otp);

    Optional<OTPPass> findByVisitorPreApprovalId(Long approvalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OTPPass> findWithLockByOtp(String otp);
}
