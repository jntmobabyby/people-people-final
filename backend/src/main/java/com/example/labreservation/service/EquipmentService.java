package com.example.labreservation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labreservation.common.BusinessException;
import com.example.labreservation.domain.EquipmentStatus;
import com.example.labreservation.dto.EquipmentCreateRequest;
import com.example.labreservation.entity.Equipment;
import com.example.labreservation.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;

    public EquipmentService(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    public List<Equipment> list(String keyword, String category, Integer status) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Equipment::getName, keyword).or().like(Equipment::getAssetNo, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Equipment::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Equipment::getStatus, status);
        }
        wrapper.orderByAsc(Equipment::getId);
        return equipmentMapper.selectList(wrapper);
    }

    public Equipment create(EquipmentCreateRequest request) {
        if (request == null || !StringUtils.hasText(request.assetNo()) || !StringUtils.hasText(request.name())) {
            throw new BusinessException("设备编号和设备名称不能为空");
        }
        Long count = equipmentMapper.selectCount(new LambdaQueryWrapper<Equipment>()
                .eq(Equipment::getAssetNo, request.assetNo()));
        if (count > 0) {
            throw new BusinessException("设备编号已存在");
        }
        Equipment equipment = new Equipment();
        equipment.setAssetNo(request.assetNo());
        equipment.setName(request.name());
        equipment.setCategory(request.category());
        equipment.setLocation(request.location());
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipmentMapper.insert(equipment);
        return equipment;
    }

    public Equipment updateStatus(Long id, Integer status) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
        return equipment;
    }
}
