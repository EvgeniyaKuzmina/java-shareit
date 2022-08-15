package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * класс для работы с Item возвращающий сущность пользователем
 */

@Data
@Builder
public class ItemDto {
    Collection<CommentDto> comments;
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private String request;
    private LastOrNextBooking lastBooking;
    private LastOrNextBooking nextBooking;

    @Data
    @Builder
    public static class ItemDtoWithComments {
        private Long id;
        private String name;
        private String description;
        private Long ownerId;
        private Boolean available;
        private String request;
        private LastOrNextBooking lastBooking;
        private LastOrNextBooking nextBooking;
        private Collection<CommentDto> comments;
    }

    @Data
    @Builder
    public static class LastOrNextBooking {
        Long id;
        Long bookerId;
        LocalDateTime start;
        LocalDateTime end;
    }


}
