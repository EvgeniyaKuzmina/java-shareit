package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * класс для работы с ItemRequest возвращающий сущность пользователем
 */

@Getter
@Setter
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
