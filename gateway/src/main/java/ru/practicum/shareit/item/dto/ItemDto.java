package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemDto {
    private Collection<CommentDto> comments;
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
        private Long id;
        private Long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }

    @Data
    @Builder
    public static class ItemRequest {
        private Long id;
        private String description;
    }


}
