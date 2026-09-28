package com.visitorpass.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiryScheduler {

    private final VisitorPreApprovalService approvalService;

    public ExpiryScheduler(VisitorPreApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @Scheduled(fixedDelay = 60000)
    public void expirePasses() {
        approvalService.expirePendingApprovals();
    }
}
