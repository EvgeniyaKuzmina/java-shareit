package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemDto createItem(ItemDto itemDto, Long id) throws ObjectNotFountException {
        UserDto userDto = userService.getUserById(id); // проверяем что пользователь с таким id существует
        return itemRepository.createItem(itemDto, UserMapper.toUser(userDto));
    }


    public ItemDto updateItem(ItemDto itemDto, Long id, Long userId) throws ObjectNotFountException {
        userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = itemRepository.getItems().get(id);
        if (!Objects.equals(item.getOwner().getId(), userId)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemService.updateItem: Указан неверный id владельца вещи");
            throw new ObjectNotFountException("Указан неверный id  владельца вещи");
        }
        return itemRepository.updateItem(itemDto, id);
    }

    public void removeItem(Long id, Long userId) throws ValidationException, ArgumentNotValidException, ObjectNotFountException {
        getItemById(id); // проверяем что вещь с таким id существует
        userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = itemRepository.getItems().get(id);
        if (!Objects.equals(item.getOwner().getId(), id)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemService.removeItem: Указан неверный id владельца вещи");
            throw new ArgumentNotValidException("Указан неверный id  владельца вещи");
        }
        itemRepository.removeItem(id);
    }

    public ItemDto getItemById(Long id) throws ValidationException {
        Optional<ItemDto> itemDto = itemRepository.getItemById(id);
        itemDto.orElseThrow(() -> {
            log.error("ItemService.getItemById: Вещи с таким id нет ");
            return new ValidationException("Вещи с таким id нет");
        });

        return itemDto.get();
    }


    public Collection<ItemDto> getAllItem(Long id) throws ObjectNotFountException {
        userService.getUserById(id); // проверяем что пользователь с таким id существует
        Collection<ItemDto> itemsByOwnerId = new ArrayList<>();
        itemRepository.getItems().values().forEach(i -> {
            if (i.getOwner().getId().equals(id)) {
                itemsByOwnerId.add(ItemMapper.toItemDto(i));
            }
        });
        return itemsByOwnerId;
    }


    public Collection<ItemDto> searchItemByTitle(String text) {
        return itemRepository.searchItemByTitle(text);
    }
}
