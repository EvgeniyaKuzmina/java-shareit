package ru.practicum.shareit.item;


import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    // создание вещи
    Item createItem(ItemDto itemDto, Long id);

    // изменение вещи
    Item updateItem(ItemDto itemDto, Long id, Long userId);

    // удаление вещи по id
    void removeItem(Long id, Long userId);

    // Просмотр информации о конкретной вещи по её идентификатору
    Item getItemById(Long id);

    // Просмотр владельцем списка всех его вещей
    Collection<Item> getAllItemByUserId(Long id, Pageable pageable);

    Collection<Item> getAllItemByUserIdWithoutPagination(Long id);

    //Поиск вещи потенциальным арендатором по части названия или описания
    Collection<Item> searchItemByNameOrDescription(String text, Pageable pageable);

    // добавление комментария к вещи после бронирования
    Comment addNewComment(CommentDto commentDto, Long userId, Long itemId, Collection<Booking> bookings);

    // получение списка запросов вещи по id запроса
    Collection<Item> findAllByRequestId(Long requestId);
}
