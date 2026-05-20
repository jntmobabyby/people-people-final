package com.example.labreservation;

import com.example.labreservation.common.BusinessException;
import com.example.labreservation.domain.ReservationStatus;
import com.example.labreservation.dto.ReservationCreateRequest;
import com.example.labreservation.entity.Reservation;
import com.example.labreservation.service.ReservationService;
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
class ReservationRegressionTests {
    @Autowired
    ReservationService reservationService;

    @Test
    void cancelingReservationAllowsSameTimeSlotToBeReservedAgain() {
        var start = LocalDateTime.of(2026, 5, 21, 9, 0);
        Reservation reservation = reservationService.create(2L,
                new ReservationCreateRequest(2L, start, start.plusHours(2)));

        reservationService.cancel(reservation.getId(), 2L);

        Reservation next = reservationService.create(3L,
                new ReservationCreateRequest(2L, start, start.plusHours(2)));

        assertThat(next.getId()).isNotNull();
        assertThat(next.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void studentCannotCancelAnotherStudentsReservation() {
        var start = LocalDateTime.of(2026, 5, 21, 14, 0);
        Reservation reservation = reservationService.create(2L,
                new ReservationCreateRequest(2L, start, start.plusHours(2)));

        assertThatThrownBy(() -> reservationService.cancel(reservation.getId(), 3L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("只能取消自己的预约");
    }
}
