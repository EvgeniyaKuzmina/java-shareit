package ru.practicum.shareit.comments.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * класс для работы с Comments возвращающий сущность пользователем
 */
@Data
@Builder
public class CommentsDto {
    private Long id;
    private String text;
    private Item item;
    private User owner;
    private LocalDate creat;
}
