package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;


/**
 класс преобразовывающий сущность бронирования в Dto и обратно
 */
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }
}
