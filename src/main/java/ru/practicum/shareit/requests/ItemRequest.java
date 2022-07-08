package ru.practicum.shareit.requests;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * класс, отвечающий за запрос вещи
 */

@Data
@Builder
public class ItemRequest {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
