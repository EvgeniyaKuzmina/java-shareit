package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * класс для работы с Comment возвращающий сущность пользователем
 */
@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime creat;
}
