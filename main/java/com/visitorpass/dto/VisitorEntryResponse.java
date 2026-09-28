package com.visitorpass.dto;

import java.time.LocalDateTime;

public record VisitorEntryResponse(
        String message,
        Long approvalId,
        String visitorName,
        String residentName,
        String flatNumber,
        LocalDateTime entryTime
) {
}
