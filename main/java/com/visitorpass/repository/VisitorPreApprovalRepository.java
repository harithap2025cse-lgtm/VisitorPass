package com.visitorpass.repository;

import com.visitorpass.entity.ApprovalStatus;
import com.visitorpass.entity.VisitorPreApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorPreApprovalRepository extends JpaRepository<VisitorPreApproval, Long> {
    List<VisitorPreApproval> findByResidentIdOrderByCreatedAtDesc(Long residentId);
    List<VisitorPreApproval> findByStatusAndExpectedExitTimeBefore(ApprovalStatus status, java.time.LocalDateTime time);
}
