package ru.practicum.shareit.booking;

/**
 статусы бронирований вещи
 */

public enum Status {
    WAITING, // новое бронирование, ожидает одобрения
    APPROVED, //бронирование подтверждено владельцем
    REJECTED, // бронирование отклонено владельцем
    CANCELED //бронирование отменено создателем
}
