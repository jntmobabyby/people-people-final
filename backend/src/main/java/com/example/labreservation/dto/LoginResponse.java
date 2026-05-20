package com.example.labreservation.dto;

public record LoginResponse(Long userId, String username, String name, String role, String token) {
}
