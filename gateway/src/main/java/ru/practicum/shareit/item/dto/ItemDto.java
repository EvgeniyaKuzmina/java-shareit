package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemDto {
    Collection<CommentDto> comments;
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private Long requestId;
    private LastOrNextBooking lastBooking;
    private LastOrNextBooking nextBooking;


    @Data
    @Builder
    public static class LastOrNextBooking {
        Long id;
        Long bookerId;
        LocalDateTime start;
        LocalDateTime end;
    }

    @Data
    @Builder
    public static class ItemRequest {
        private Long id;
        private String description;
    }


}
