package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;


/**
 * класс преобразовывающий сущность бронирования в Dto и обратно
 */

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(BookingDto.Item.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .status(booking.getStatus())
                .booker(BookingDto.Booker.builder().id(booking.getBooker().getId()).build())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        Booking booking = Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .item(item)
                .end(bookingDto.getEnd())
                .booker(user)
                .build();
        if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }
        return booking;
    }

}
