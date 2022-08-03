package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // поиск всех вещей по id владельца
    Collection<Item> findAllByOwnerId(Long ownerId);

    // поиск вещи по содержащейся подстроке в названии или описании вещи.
    Collection<Item> findByDescriptionOrNameContainingIgnoreCase(String nameSearch, String descriptionSearch);
/*
    // создание вещи
    ItemDto createItem(ItemDto itemDto, User user);

    // изменение вещи
    ItemDto updateItem(ItemDto itemDto, Long id);

    // удаление вещи по id
    void removeItem(Long id);

    // Просмотр информации о конкретной вещи по её идентификатору
    Optional<ItemDto> getItemById(Long id);

    // Просмотр владельцем списка всех его вещей
    Collection<ItemDto> getAllItem();

    //Поиск вещи потенциальным арендатором по части названия или описания
    Collection<ItemDto> searchItemByTitle(String text);

    Map<Long, Item> getItems();*/
}

