package com.example.labreservation.controller;

import com.example.labreservation.common.Result;
import com.example.labreservation.dto.EquipmentCreateRequest;
import com.example.labreservation.dto.EquipmentStatusRequest;
import com.example.labreservation.entity.Equipment;
import com.example.labreservation.service.EquipmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public Result<List<Equipment>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.ok(equipmentService.list(keyword, category, status));
    }

    @PostMapping
    public Result<Equipment> create(@RequestBody EquipmentCreateRequest request) {
        return Result.ok(equipmentService.create(request));
    }

    @PatchMapping("/{id}/status")
    public Result<Equipment> updateStatus(@PathVariable Long id, @RequestBody EquipmentStatusRequest request) {
        return Result.ok(equipmentService.updateStatus(id, request.status()));
    }
}
