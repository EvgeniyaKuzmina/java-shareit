package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * класс для работы с ItemRequest возвращающий сущность пользователем
 */

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
    private Collection<Item> items;

    @Data
    @Builder
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}
