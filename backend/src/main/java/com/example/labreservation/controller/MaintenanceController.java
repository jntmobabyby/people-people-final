package com.example.labreservation.controller;

import com.example.labreservation.common.Result;
import com.example.labreservation.dto.MaintenanceCreateRequest;
import com.example.labreservation.entity.MaintenanceTicket;
import com.example.labreservation.service.MaintenanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/maintenance-tickets")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public Result<List<MaintenanceTicket>> list() {
        return Result.ok(maintenanceService.list());
    }

    @PostMapping
    public Result<MaintenanceTicket> create(@RequestBody MaintenanceCreateRequest request) {
        return Result.ok(maintenanceService.create(request));
    }
}
