package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 класс для работы с Item возвращающий сущность пользователем
 */

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
