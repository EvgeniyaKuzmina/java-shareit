package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * контроллер для работы с объектом вещь
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id владельца вещи

    private final ItemServiceImpl itemServiceImpl;

    // создание вещи
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ArgumentNotValidException, ObjectNotFountException {
        if (itemDto.getAvailable() == null || itemDto.getName() == null ||
                itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            log.warn("Не указано имя, описание товара или параметр доступности");
            throw new ArgumentNotValidException("Не указано имя, описание товара или параметр доступности");
        }
        Item item =  itemServiceImpl.createItem(ItemMapper.toItem(itemDto, userId), userId);
        return ItemMapper.toItemDto(item);
    }

    // обновление данных о вещи
    @PatchMapping(value = {"/{id}"})
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @PathVariable Long id,
                              @RequestHeader(HEADER_REQUEST) Long userId) throws ObjectNotFountException, ValidationException {
        Item item =  itemServiceImpl.updateItem(ItemMapper.toItem(itemDto, userId), id, userId);
        return ItemMapper.toItemDto(item);
    }

    //удаление вещи
    @DeleteMapping(value = {"/{id}"})
    public void removeItem(@PathVariable Long id, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ValidationException, ArgumentNotValidException, ObjectNotFountException {
        itemServiceImpl.removeItem(id, userId);
    }

    // получение вещи по id
    @GetMapping(value = {"/{id}"})
    public ItemDto getItemById(@PathVariable Long id) throws ValidationException {
        return ItemMapper.toItemDto(itemServiceImpl.getItemById(id));
    }

    // получение владельцем списка всех его вещей
    @GetMapping
    public Collection<ItemDto> getAllItem(@RequestHeader(HEADER_REQUEST) Long userId)
            throws ObjectNotFountException {
        Collection<ItemDto> allItemDto = new ArrayList<>();
        itemServiceImpl.getAllItemByUserId(userId).forEach(i -> allItemDto.add(ItemMapper.toItemDto(i)));
        return allItemDto;
    }


    // поиск вещи по части строки в названии или в описании
    @GetMapping("/search")
    public Collection<ItemDto> searchItemByNameOrDescription(@RequestParam String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        Collection<ItemDto> itemsDtoContaining = new ArrayList<>();
        itemServiceImpl.searchItemByNameOrDescription(text).forEach(i -> itemsDtoContaining.add(ItemMapper.toItemDto(i)));
        return itemsDtoContaining;
    }
}
