package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * класс описывающий бронирование вещи
 */

@Data
@Builder
public class Booking {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
