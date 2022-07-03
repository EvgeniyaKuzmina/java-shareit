package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

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

    public ItemDto createItem(ItemDto itemDto, Long id) throws ValidationException {
        User user = userService.getUserById(id); // проверяем что пользователь с таким id существует
        return itemRepository.createItem(itemDto, user);
    }


    public ItemDto updateItem(ItemDto itemDto, Long id) throws ValidationException, ArgumentNotValidException {
        userService.getUserById(id); // проверяем что пользователь с таким id существует
        Item item = itemRepository.getItems().get(itemDto.getId());
        if (!Objects.equals(item.getOwner().getId(), id)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemService.updateItem: Указан неверный id {} владельца вещи", id);
            throw new ArgumentNotValidException("Указан неверный id " + id + " владельца вещи");
        }
        return itemRepository.updateItem(itemDto);
    }

    public void removeItem(Long id) throws ValidationException {
        getItemById(id); // проверяем что вещь с таким id существует
        itemRepository.removeItem(id);
    }

    public ItemDto getItemById(Long id) throws ValidationException {
        Optional<ItemDto> itemDto = itemRepository.getItemById(id);
        itemDto.orElseThrow(() -> {
            log.error("ItemService.getItemById: Вещи с таким id {} нет ", id);
            return new ValidationException("Вещи с таким id нет");
        });

        return itemDto.get();
    }


    public Collection<ItemDto> getAllItem(Long id) throws ValidationException {
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
