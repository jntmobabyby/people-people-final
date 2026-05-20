package com.example.labreservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReturnCreateRequest(Long reservationId, LocalDateTime returnTime, String conditionNote, BigDecimal penaltyAmount) {
}
