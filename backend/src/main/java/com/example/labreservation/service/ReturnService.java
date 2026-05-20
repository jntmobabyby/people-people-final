package com.example.labreservation.service;

import com.example.labreservation.common.BusinessException;
import com.example.labreservation.domain.EquipmentStatus;
import com.example.labreservation.domain.ReservationStatus;
import com.example.labreservation.dto.ReturnCreateRequest;
import com.example.labreservation.entity.Equipment;
import com.example.labreservation.entity.Reservation;
import com.example.labreservation.entity.ReturnRecord;
import com.example.labreservation.mapper.EquipmentMapper;
import com.example.labreservation.mapper.ReservationMapper;
import com.example.labreservation.mapper.ReturnRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ReturnService {
    private final ReturnRecordMapper returnRecordMapper;
    private final ReservationMapper reservationMapper;
    private final EquipmentMapper equipmentMapper;

    public ReturnService(ReturnRecordMapper returnRecordMapper, ReservationMapper reservationMapper, EquipmentMapper equipmentMapper) {
        this.returnRecordMapper = returnRecordMapper;
        this.reservationMapper = reservationMapper;
        this.equipmentMapper = equipmentMapper;
    }

    @Transactional
    public ReturnRecord create(ReturnCreateRequest request) {
        if (request == null || request.reservationId() == null) {
            throw new BusinessException("预约记录不能为空");
        }
        Reservation reservation = reservationMapper.selectById(request.reservationId());
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new BusinessException("只有已审批预约可以登记归还");
        }
        ReturnRecord record = new ReturnRecord();
        record.setReservationId(request.reservationId());
        record.setReturnTime(request.returnTime() == null ? LocalDateTime.now() : request.returnTime());
        record.setConditionNote(request.conditionNote());
        record.setPenaltyAmount(request.penaltyAmount() == null ? BigDecimal.ZERO : request.penaltyAmount());
        returnRecordMapper.insert(record);

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationMapper.updateById(reservation);

        Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
            equipmentMapper.updateById(equipment);
        }
        return record;
    }
}
