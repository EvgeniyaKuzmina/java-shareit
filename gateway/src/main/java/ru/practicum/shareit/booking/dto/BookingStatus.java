package ru.practicum.shareit.booking.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum BookingStatus {

    WAITING("WAITING"), // новое бронирование, ожидает одобрения
    APPROVED("APPROVED"), //бронирование подтверждено владельцем
    REJECTED("REJECTED"), // бронирование отклонено владельцем
    CANCELED("CANCELED"), //бронирование отменено создателем
    CURRENT("CURRENT"), // текущее бронирование
    PAST("PAST"), // прошедшее бронирование
    FUTURE("FUTURE"), // будущее бронирование
    ALL("ALL"); // все бронирования

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public static Optional<BookingStatus> from(String stringState) {
        for (BookingStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

}
