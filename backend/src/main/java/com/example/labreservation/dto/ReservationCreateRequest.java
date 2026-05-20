package com.example.labreservation.dto;

import java.time.LocalDateTime;

public record ReservationCreateRequest(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
}
