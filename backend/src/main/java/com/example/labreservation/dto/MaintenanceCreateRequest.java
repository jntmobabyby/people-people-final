package com.example.labreservation.dto;

public record MaintenanceCreateRequest(Long equipmentId, Long reporterId, String faultDesc) {
}
