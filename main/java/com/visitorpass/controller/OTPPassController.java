package com.visitorpass.controller;

import com.visitorpass.dto.OTPValidationRequest;
import com.visitorpass.dto.VisitorEntryResponse;
import com.visitorpass.entity.OTPPass;
import com.visitorpass.service.OTPPassService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OTPPassController {

    private final OTPPassService otpPassService;

    public OTPPassController(OTPPassService otpPassService) {
        this.otpPassService = otpPassService;
    }

    @PostMapping("/validate")
    public VisitorEntryResponse validate(@Valid @RequestBody OTPValidationRequest request) {
        return otpPassService.validateEntry(request);
    }

    @GetMapping("/approval/{approvalId}")
    public OTPPass getByApprovalId(@PathVariable Long approvalId) {
        return otpPassService.getByApprovalId(approvalId);
    }
}
