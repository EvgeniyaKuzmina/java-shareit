package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Email;
import java.time.LocalDate;

/**
 * класс для работы с Booking возвращающий сущность пользователем
 */

@Builder
@Data
public class BookingDto {
    private final Long id;
    private final LocalDate start;
    private final LocalDate end;
    private final Long itemId;
    private final Long bookerId;
    private String itemName;
    private final Status status;


    @Data
    @Builder
    static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long itemId;
        private String itemName;
    }

    @Data
    @Builder
    static class User {
        private Long id;
        private String name;
        @Email
        @UniqueElements
        private String email;
    }
}
