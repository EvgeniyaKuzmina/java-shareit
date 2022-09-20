package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

/**
 * статусы бронирований вещи
 */

public enum Status {
    WAITING("WAITING"), // новое бронирование, ожидает одобрения
    APPROVED("APPROVED"), //бронирование подтверждено владельцем
    REJECTED("REJECTED"), // бронирование отклонено владельцем
    CANCELED("CANCELED"), //бронирование отменено создателем
    CURRENT("CURRENT"), // текущее бронирование
    PAST("PAST"), // прошедшее бронирование
    FUTURE("FUTURE"), // будущее бронирование
    ALL("ALL"); // все бронирования


    private final String status;

    Status(String status) {
        this.status = status;
    }

    public static Status getStatus(String status) throws ValidationException {
        switch (status) {
            case "WAITING":
                return WAITING;
            case "APPROVED":
                return APPROVED;
            case "REJECTED":
                return REJECTED;
            case "CANCELED":
                return CANCELED;
            case "CURRENT":
                return CURRENT;
            case "PAST":
                return PAST;
            case "FUTURE":
                return FUTURE;
            case "ALL":
                return ALL;
            default:
                throw new ValidationException("Unknown state: " + status);
        }
    }
}
