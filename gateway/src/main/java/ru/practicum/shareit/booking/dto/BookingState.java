package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.ValidationException;

import java.util.Optional;

public enum BookingState {

    WAITING("WAITING"), // новое бронирование, ожидает одобрения
    APPROVED("APPROVED"), //бронирование подтверждено владельцем
    REJECTED("REJECTED"), // бронирование отклонено владельцем
    CANCELED("CANCELED"), //бронирование отменено создателем
    CURRENT("CURRENT"), // текущее бронирование
    PAST("PAST"), // прошедшее бронирование
    FUTURE("FUTURE"), // будущее бронирование
    ALL("ALL"); // все бронирования

    private final String status;

    BookingState(String status) {
        this.status = status;
    }

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

    public static BookingState getState(String state) throws ValidationException {
        switch (state.toUpperCase()) {
            case "ALL":
                return ALL;
            case "CURRENT":
                return CURRENT;
            case "PAST":
                return PAST;
            case "FUTURE":
                return FUTURE;
            case "WAITING":
                return WAITING;
            case "REJECTED":
                return REJECTED;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }
}
