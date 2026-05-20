package com.example.labreservation.domain;

public final class ReservationStatus {
    public static final int PENDING = 0;
    public static final int APPROVED = 1;
    public static final int CANCELED = 2;
    public static final int COMPLETED = 3;
    public static final int REJECTED = 4;

    private ReservationStatus() {
    }
}
