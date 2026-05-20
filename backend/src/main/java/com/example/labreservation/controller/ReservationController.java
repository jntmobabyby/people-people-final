package com.example.labreservation.controller;

import com.example.labreservation.common.Result;
import com.example.labreservation.dto.ApproveReservationRequest;
import com.example.labreservation.dto.ReservationCreateRequest;
import com.example.labreservation.entity.Reservation;
import com.example.labreservation.service.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public Result<List<Reservation>> list(@RequestParam(required = false) Long userId) {
        return Result.ok(reservationService.list(userId));
    }

    @PostMapping
    public Result<Reservation> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody ReservationCreateRequest request) {
        return Result.ok(reservationService.create(userId, request));
    }

    @PostMapping("/{id}/approve")
    public Result<Reservation> approve(@PathVariable Long id, @RequestBody(required = false) ApproveReservationRequest request) {
        String comment = request == null ? null : request.reviewComment();
        return Result.ok(reservationService.approve(id, comment));
    }

    @PostMapping("/{id}/cancel")
    public Result<Reservation> cancel(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Result.ok(reservationService.cancel(id, userId));
    }
}
