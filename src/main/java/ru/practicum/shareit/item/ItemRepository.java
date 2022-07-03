package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ItemRepository {

    // создание вещи
    ItemDto createItem(ItemDto itemDto, User user);

    // изменение вещи
    ItemDto updateItem(ItemDto itemDto);

    // удаление вещи по id
    void removeItem(Long id);

    // Просмотр информации о конкретной вещи по её идентификатору
    Optional<ItemDto> getItemById(Long id);

    // Просмотр владельцем списка всех его вещей
    Collection<ItemDto> getAllItem();

    //Поиск вещи потенциальным арендатором по части названия или описания
    Collection<ItemDto> searchItemByTitle(String text);

    Map<Long, Item> getItems();
}

