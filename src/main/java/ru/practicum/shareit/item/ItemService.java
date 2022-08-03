package ru.practicum.shareit.item;


import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public interface ItemService {
    Item createItem(Item itemDto, Long id) throws ObjectNotFountException;


    Item updateItem(Item itemDto, Long id, Long userId) throws ObjectNotFountException, ValidationException;

    void removeItem(Long id, Long userId) throws ValidationException, ArgumentNotValidException, ObjectNotFountException;

    Item getItemById(Long id) throws ValidationException;

    Collection<Item> getAllItemByUserId(Long id) throws ObjectNotFountException;

    Collection<Item> searchItemByNameOrDescription(String text);
}
