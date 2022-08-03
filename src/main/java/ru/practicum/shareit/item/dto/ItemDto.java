package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * класс для работы с Item возвращающий сущность пользователем
 */

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private String request;
}
