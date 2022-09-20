package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * класс для работы с Booking возвращающий сущность пользователем
 */

@Builder
@Data
public class BookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final Long itemId;
    private final Booker booker;
    private final BookingState status;


    @Data
    @Builder
    public static class Item {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class Booker {
        private Long id;
    }

}
