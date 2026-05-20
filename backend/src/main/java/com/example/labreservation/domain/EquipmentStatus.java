package com.example.labreservation.domain;

public final class EquipmentStatus {
    public static final int DISABLED = 0;
    public static final int AVAILABLE = 1;
    public static final int RESERVED = 2;
    public static final int MAINTENANCE = 3;

    private EquipmentStatus() {
    }
}
