package com.example.labreservation.controller;

import com.example.labreservation.common.Result;
import com.example.labreservation.dto.ReturnCreateRequest;
import com.example.labreservation.entity.ReturnRecord;
import com.example.labreservation.service.ReturnService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/returns")
public class ReturnController {
    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    public Result<ReturnRecord> create(@RequestBody ReturnCreateRequest request) {
        return Result.ok(returnService.create(request));
    }
}
