package com.example.labreservation;

import com.example.labreservation.common.BusinessException;
import com.example.labreservation.domain.EquipmentStatus;
import com.example.labreservation.domain.ReservationStatus;
import com.example.labreservation.dto.LoginRequest;
import com.example.labreservation.dto.MaintenanceCreateRequest;
import com.example.labreservation.dto.ReservationCreateRequest;
import com.example.labreservation.dto.ReturnCreateRequest;
import com.example.labreservation.entity.Equipment;
import com.example.labreservation.entity.Reservation;
import com.example.labreservation.mapper.EquipmentMapper;
import com.example.labreservation.service.AuthService;
import com.example.labreservation.service.MaintenanceService;
import com.example.labreservation.service.ReservationService;
import com.example.labreservation.service.ReturnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LabReservationApplicationTests {
    @Autowired
    AuthService authService;
    @Autowired
    ReservationService reservationService;
    @Autowired
    ReturnService returnService;
    @Autowired
    MaintenanceService maintenanceService;
    @Autowired
    EquipmentMapper equipmentMapper;

    @Test
    void loginSucceedsWithCorrectPassword() {
        var response = authService.login(new LoginRequest("stu001", "123456"));

        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.role()).isEqualTo("STUDENT");
        assertThat(response.token()).startsWith("demo-token-2");
    }

    @Test
    void loginFailsWithWrongPassword() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("stu001", "bad-password")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名或密码错误");
    }

    @Test
    void maintenanceEquipmentCannotBeReserved() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);
        var request = new ReservationCreateRequest(3L, start, start.plusHours(2));

        assertThatThrownBy(() -> reservationService.create(2L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("设备当前不可预约");
    }

    @Test
    void reservationSucceedsWhenEquipmentIsAvailable() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);

        Reservation reservation = reservationService.create(2L, new ReservationCreateRequest(1L, start, start.plusHours(2)));

        assertThat(reservation.getId()).isNotNull();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void overlappingReservationForSameEquipmentFails() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);
        reservationService.create(2L, new ReservationCreateRequest(1L, start, start.plusHours(2)));

        assertThatThrownBy(() -> reservationService.create(3L,
                new ReservationCreateRequest(1L, start.plusMinutes(30), start.plusHours(3))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("该设备在所选时间段已有预约");
    }

    @Test
    void adjacentReservationForSameEquipmentSucceeds() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);
        reservationService.create(2L, new ReservationCreateRequest(1L, start, start.plusHours(2)));

        Reservation next = reservationService.create(3L,
                new ReservationCreateRequest(1L, start.plusHours(2), start.plusHours(4)));

        assertThat(next.getId()).isNotNull();
    }

    @Test
    void approvingReservationKeepsEquipmentAvailableForOtherTimeSlots() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);
        Reservation reservation = reservationService.create(2L, new ReservationCreateRequest(1L, start, start.plusHours(2)));

        reservationService.approve(reservation.getId(), "同意借用");

        Equipment equipment = equipmentMapper.selectById(1L);
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
    }

    @Test
    void adjacentReservationStillSucceedsAfterPreviousReservationApproved() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);
        Reservation reservation = reservationService.create(2L, new ReservationCreateRequest(1L, start, start.plusHours(2)));
        reservationService.approve(reservation.getId(), "同意借用");

        Reservation next = reservationService.create(3L,
                new ReservationCreateRequest(1L, start.plusHours(2), start.plusHours(4)));

        assertThat(next.getId()).isNotNull();
    }

    @Test
    void returningApprovedReservationRestoresEquipmentAvailability() {
        var start = LocalDateTime.of(2026, 5, 20, 10, 0);
        Reservation reservation = reservationService.create(2L, new ReservationCreateRequest(1L, start, start.plusHours(2)));
        reservationService.approve(reservation.getId(), "同意借用");

        returnService.create(new ReturnCreateRequest(reservation.getId(), start.plusHours(2), "设备完好", null));

        Equipment equipment = equipmentMapper.selectById(1L);
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
    }

    @Test
    void maintenanceTicketChangesEquipmentToMaintenanceStatus() {
        maintenanceService.create(new MaintenanceCreateRequest(1L, 2L, "开机后风扇异常响动"));

        Equipment equipment = equipmentMapper.selectById(1L);
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.MAINTENANCE);
    }
}
