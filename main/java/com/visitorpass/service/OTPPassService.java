package com.visitorpass.service;

import com.visitorpass.dto.OTPValidationRequest;
import com.visitorpass.dto.VisitorEntryResponse;
import com.visitorpass.entity.ApprovalStatus;
import com.visitorpass.entity.OTPPass;
import com.visitorpass.entity.VisitorPreApproval;
import com.visitorpass.exception.BusinessRuleException;
import com.visitorpass.exception.ResourceNotFoundException;
import com.visitorpass.repository.OTPPassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OTPPassService {

    private final OTPPassRepository otpPassRepository;

    public OTPPassService(OTPPassRepository otpPassRepository) {
        this.otpPassRepository = otpPassRepository;
    }

    @Transactional
    public VisitorEntryResponse validateEntry(OTPValidationRequest request) {
        OTPPass pass = otpPassRepository.findWithLockByOtp(request.otp())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP."));

        VisitorPreApproval approval = pass.getVisitorPreApproval();
        LocalDateTime now = LocalDateTime.now();

        if (pass.isUsed()) {
            throw new BusinessRuleException(
                    "This OTP has already been used for entry.");
        }

        if (approval.getStatus() == ApprovalStatus.REVOKED) {
            throw new BusinessRuleException(
                    "This visitor approval has been revoked.");
        }

        if (approval.getStatus() == ApprovalStatus.EXPIRED) {
            throw new BusinessRuleException(
                    "This visitor approval has expired.");
        }

        if (approval.getStatus() == ApprovalStatus.USED) {
            throw new BusinessRuleException(
                    "This visitor approval has already been used.");
        }

        if (now.isBefore(approval.getVisitDateTime())) {
            throw new BusinessRuleException(
                    "Visitor entry is not allowed before the expected visit time.");
        }

        if (now.isAfter(approval.getExpectedExitTime()) ||
                now.isAfter(pass.getExpiresAt())) {
            approval.setStatus(ApprovalStatus.EXPIRED);
            otpPassRepository.save(pass);

            throw new BusinessRuleException(
                    "This visitor pass has expired.");
        }

        pass.setUsed(true);
        pass.setValidatedAt(now);

        approval.setStatus(ApprovalStatus.USED);
        approval.setEntryTime(now);

        otpPassRepository.save(pass);

        return new VisitorEntryResponse(
                "Visitor entry approved",
                approval.getId(),
                approval.getVisitorName(),
                approval.getResident().getName(),
                approval.getResident().getFlat().getFlatNumber(),
                now
        );
    }

    public OTPPass getByApprovalId(Long approvalId) {
        return otpPassRepository.findByVisitorPreApprovalId(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OTP pass not found for approval id: " + approvalId));
    }
}
