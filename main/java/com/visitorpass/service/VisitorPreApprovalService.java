package com.visitorpass.service;

import com.visitorpass.dto.PreApprovalRequest;
import com.visitorpass.entity.ApprovalStatus;
import com.visitorpass.entity.OTPPass;
import com.visitorpass.entity.Resident;
import com.visitorpass.entity.VisitorPreApproval;
import com.visitorpass.exception.BusinessRuleException;
import com.visitorpass.exception.ResourceNotFoundException;
import com.visitorpass.repository.OTPPassRepository;
import com.visitorpass.repository.ResidentRepository;
import com.visitorpass.repository.VisitorPreApprovalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorPreApprovalService {

    private final VisitorPreApprovalRepository approvalRepository;
    private final ResidentRepository residentRepository;
    private final OTPPassRepository otpPassRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public VisitorPreApprovalService(
            VisitorPreApprovalRepository approvalRepository,
            ResidentRepository residentRepository,
            OTPPassRepository otpPassRepository) {
        this.approvalRepository = approvalRepository;
        this.residentRepository = residentRepository;
        this.otpPassRepository = otpPassRepository;
    }

    @Transactional
    public VisitorPreApproval create(PreApprovalRequest request) {
        if (!request.expectedExitTime().isAfter(request.visitDateTime())) {
            throw new BusinessRuleException(
                    "Expected exit time must be after the visit start time.");
        }

        Resident resident = residentRepository.findById(request.residentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resident not found with id: " + request.residentId()));

        VisitorPreApproval approval = new VisitorPreApproval();
        approval.setVisitorName(request.visitorName());
        approval.setVisitorPhone(request.visitorPhone());
        approval.setVisitDateTime(request.visitDateTime());
        approval.setExpectedExitTime(request.expectedExitTime());
        approval.setPurpose(request.purpose());
        approval.setStatus(ApprovalStatus.PENDING);
        approval.setResident(resident);

        OTPPass otpPass = new OTPPass();
        otpPass.setOtp(generateUniqueOtp());
        otpPass.setGeneratedAt(LocalDateTime.now());
        otpPass.setExpiresAt(request.expectedExitTime());
        otpPass.setUsed(false);
        otpPass.setVisitorPreApproval(approval);

        approval.setOtpPass(otpPass);

        return approvalRepository.save(approval);
    }

    @Transactional
    public VisitorPreApproval getById(Long id) {
        VisitorPreApproval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Visitor pre-approval not found with id: " + id));

        if (expireIfNecessary(approval)) {
            approvalRepository.save(approval);
        }

        return approval;
    }

    @Transactional
    public List<VisitorPreApproval> getByResident(Long residentId) {
        if (!residentRepository.existsById(residentId)) {
            throw new ResourceNotFoundException(
                    "Resident not found with id: " + residentId);
        }

        List<VisitorPreApproval> approvals =
                approvalRepository.findByResidentIdOrderByCreatedAtDesc(residentId);

        boolean changed = false;

        for (VisitorPreApproval approval : approvals) {
            if (expireIfNecessary(approval)) {
                changed = true;
            }
        }

        if (changed) {
            approvalRepository.saveAll(approvals);
        }

        return approvals;
    }

    @Transactional
    public VisitorPreApproval revoke(Long id) {
        VisitorPreApproval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Visitor pre-approval not found with id: " + id));

        expireIfNecessary(approval);

        if (approval.getStatus() == ApprovalStatus.USED) {
            throw new BusinessRuleException(
                    "A used visitor pass cannot be revoked.");
        }

        if (approval.getStatus() == ApprovalStatus.EXPIRED) {
            throw new BusinessRuleException(
                    "An expired visitor pass cannot be revoked.");
        }

        if (approval.getStatus() == ApprovalStatus.REVOKED) {
            throw new BusinessRuleException(
                    "Visitor pass is already revoked.");
        }

        approval.setStatus(ApprovalStatus.REVOKED);
        approval.setRevokedAt(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    private boolean expireIfNecessary(VisitorPreApproval approval) {
        if (approval.getStatus() == ApprovalStatus.PENDING &&
                LocalDateTime.now().isAfter(approval.getExpectedExitTime())) {

            approval.setStatus(ApprovalStatus.EXPIRED);
            return true;
        }

        return false;
    }

    private String generateUniqueOtp() {
        String otp;

        do {
            otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        } while (otpPassRepository.findByOtp(otp).isPresent());

        return otp;
    }

    @Transactional
    public void expirePendingApprovals() {
        List<VisitorPreApproval> expired =
                approvalRepository.findByStatusAndExpectedExitTimeBefore(
                        ApprovalStatus.PENDING,
                        LocalDateTime.now());

        for (VisitorPreApproval approval : expired) {
            approval.setStatus(ApprovalStatus.EXPIRED);
        }

        if (!expired.isEmpty()) {
            approvalRepository.saveAll(expired);
        }
    }
}