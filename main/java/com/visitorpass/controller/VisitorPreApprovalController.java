package com.visitorpass.controller;

import com.visitorpass.dto.PreApprovalRequest;
import com.visitorpass.entity.VisitorPreApproval;
import com.visitorpass.service.VisitorPreApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class VisitorPreApprovalController {

    private final VisitorPreApprovalService approvalService;

    public VisitorPreApprovalController(VisitorPreApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitorPreApproval create(@Valid @RequestBody PreApprovalRequest request) {
        return approvalService.create(request);
    }

    @GetMapping("/{id}")
    public VisitorPreApproval getById(@PathVariable Long id) {
        return approvalService.getById(id);
    }

    @GetMapping("/resident/{residentId}")
    public List<VisitorPreApproval> getByResident(@PathVariable Long residentId) {
        return approvalService.getByResident(residentId);
    }

    @PutMapping("/{id}/revoke")
    public VisitorPreApproval revoke(@PathVariable Long id) {
        return approvalService.revoke(id);
    }
}
