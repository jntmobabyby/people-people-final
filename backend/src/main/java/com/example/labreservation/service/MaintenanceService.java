package com.example.labreservation.service;

import com.example.labreservation.common.BusinessException;
import com.example.labreservation.domain.EquipmentStatus;
import com.example.labreservation.domain.MaintenanceStatus;
import com.example.labreservation.dto.MaintenanceCreateRequest;
import com.example.labreservation.entity.Equipment;
import com.example.labreservation.entity.MaintenanceTicket;
import com.example.labreservation.mapper.EquipmentMapper;
import com.example.labreservation.mapper.MaintenanceTicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceService {
    private final MaintenanceTicketMapper maintenanceTicketMapper;
    private final EquipmentMapper equipmentMapper;

    public MaintenanceService(MaintenanceTicketMapper maintenanceTicketMapper, EquipmentMapper equipmentMapper) {
        this.maintenanceTicketMapper = maintenanceTicketMapper;
        this.equipmentMapper = equipmentMapper;
    }

    public List<MaintenanceTicket> list() {
        return maintenanceTicketMapper.selectList(null);
    }

    @Transactional
    public MaintenanceTicket create(MaintenanceCreateRequest request) {
        if (request == null || request.equipmentId() == null || !StringUtils.hasText(request.faultDesc())) {
            throw new BusinessException("设备和故障描述不能为空");
        }
        Equipment equipment = equipmentMapper.selectById(request.equipmentId());
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        MaintenanceTicket ticket = new MaintenanceTicket();
        ticket.setEquipmentId(request.equipmentId());
        ticket.setReporterId(request.reporterId());
        ticket.setFaultDesc(request.faultDesc());
        ticket.setStatus(MaintenanceStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        maintenanceTicketMapper.insert(ticket);

        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipmentMapper.updateById(equipment);
        return ticket;
    }
}
