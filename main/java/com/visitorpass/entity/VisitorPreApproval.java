package com.visitorpass.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_pre_approvals")
public class VisitorPreApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String visitorName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Visitor phone must contain exactly 10 digits")
    @Column(nullable = false, length = 10)
    private String visitorPhone;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime visitDateTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expectedExitTime;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime revokedAt;

    private LocalDateTime entryTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @JsonIgnore
    @OneToOne(mappedBy = "visitorPreApproval", cascade = CascadeType.ALL, orphanRemoval = true)
    private OTPPass otpPass;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public VisitorPreApproval() {
    }

    public Long getId() {
        return id;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }

    public LocalDateTime getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(LocalDateTime visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    public LocalDateTime getExpectedExitTime() {
        return expectedExitTime;
    }

    public void setExpectedExitTime(LocalDateTime expectedExitTime) {
        this.expectedExitTime = expectedExitTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public OTPPass getOtpPass() {
        return otpPass;
    }

    public void setOtpPass(OTPPass otpPass) {
        this.otpPass = otpPass;
    }
}
