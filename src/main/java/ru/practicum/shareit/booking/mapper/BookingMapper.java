package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;


/**
 класс преобразовывающий сущность бронирования в Dto и обратно
 */
@Component
public class BookingMapper {

    private static ItemService itemService;

    @Autowired
    public BookingMapper(ItemService itemService) {
        BookingMapper.itemService = itemService;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .itemName(booking.getItem().getName())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) throws ValidationException {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }
}
