package com.example.labreservation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labreservation.common.BusinessException;
import com.example.labreservation.domain.EquipmentStatus;
import com.example.labreservation.domain.ReservationStatus;
import com.example.labreservation.dto.ReservationCreateRequest;
import com.example.labreservation.entity.Equipment;
import com.example.labreservation.entity.Reservation;
import com.example.labreservation.mapper.EquipmentMapper;
import com.example.labreservation.mapper.ReservationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationMapper reservationMapper;
    private final EquipmentMapper equipmentMapper;

    public ReservationService(ReservationMapper reservationMapper, EquipmentMapper equipmentMapper) {
        this.reservationMapper = reservationMapper;
        this.equipmentMapper = equipmentMapper;
    }

    public List<Reservation> list(Long userId) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Reservation::getUserId, userId);
        }
        wrapper.orderByDesc(Reservation::getCreatedAt);
        return reservationMapper.selectList(wrapper);
    }

    @Transactional
    public Reservation create(Long userId, ReservationCreateRequest request) {
        if (userId == null) {
            throw new BusinessException("缺少用户信息");
        }
        validateReservationRequest(request);
        Equipment equipment = equipmentMapper.selectById(request.equipmentId());
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        if (equipment.getStatus() == null || equipment.getStatus() != EquipmentStatus.AVAILABLE) {
            throw new BusinessException("设备当前不可预约");
        }
        if (hasTimeConflict(request.equipmentId(), request.startTime(), request.endTime())) {
            throw new BusinessException("该设备在所选时间段已有预约");
        }
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setEquipmentId(request.equipmentId());
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservationMapper.insert(reservation);
        return reservation;
    }

    @Transactional
    public Reservation approve(Long id, String reviewComment) {
        Reservation reservation = requireReservation(id);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException("只有待审批预约可以审批");
        }
        if (hasTimeConflictExcludingSelf(reservation.getId(), reservation.getEquipmentId(), reservation.getStartTime(), reservation.getEndTime())) {
            throw new BusinessException("审批失败：该设备时间段已被其他预约占用");
        }
        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setReviewComment(reviewComment);
        reservationMapper.updateById(reservation);
        return reservation;
    }

    @Transactional
    public Reservation cancel(Long id, Long userId) {
        Reservation reservation = requireReservation(id);
        if (userId != null && !userId.equals(reservation.getUserId())) {
            throw new BusinessException("只能取消自己的预约");
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BusinessException("已完成预约不能取消");
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        reservationMapper.updateById(reservation);
        return reservation;
    }

    private void validateReservationRequest(ReservationCreateRequest request) {
        if (request == null || request.equipmentId() == null || request.startTime() == null || request.endTime() == null) {
            throw new BusinessException("预约设备和时间不能为空");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new BusinessException("预约结束时间必须晚于开始时间");
        }
    }

    private Reservation requireReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        return reservation;
    }

    private boolean hasTimeConflict(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        return hasTimeConflictExcludingSelf(null, equipmentId, startTime, endTime);
    }

    private boolean hasTimeConflictExcludingSelf(Long reservationId, Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getEquipmentId, equipmentId)
                .in(Reservation::getStatus, ReservationStatus.PENDING, ReservationStatus.APPROVED)
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime);
        if (reservationId != null) {
            wrapper.ne(Reservation::getId, reservationId);
        }
        return reservationMapper.selectCount(wrapper) > 0;
    }
}
