package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
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

    private final ItemService itemService;

    // создание вещи
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ArgumentNotValidException, ObjectNotFountException {
        if (itemDto.getAvailable() == null || itemDto.getName() == null ||
                itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            log.warn("Не указано имя, описание товара или параметр доступности");
            throw new ArgumentNotValidException("Не указано имя, описание товара или параметр доступности");
        }
        return itemService.createItem(itemDto, userId);
    }

    // обновление данных о вещи
    @PatchMapping(value = {"/{id}"})
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @PathVariable Long id,
                              @RequestHeader(HEADER_REQUEST) Long userId) throws ObjectNotFountException {
        return itemService.updateItem(itemDto, id, userId);
    }

    //удаление вещи
    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public void removeItem(@PathVariable Long id, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ValidationException, ArgumentNotValidException, ObjectNotFountException {
        itemService.removeItem(id, userId);
    }

    // получение вещи по id
    @GetMapping(value = {"/{id}"})
    @ResponseBody
    public ItemDto getItemById(@PathVariable Long id) throws ValidationException {
        return itemService.getItemById(id);
    }

    // получение владельцем списка всех его вещей
    @GetMapping
    @ResponseBody
    public Collection<ItemDto> getAllItem(@RequestHeader(HEADER_REQUEST) Long userId)
            throws ObjectNotFountException {
        return itemService.getAllItem(userId);
    }


    // поиск вещи по части строки в названии или в описании
    @GetMapping("/search")
    @ResponseBody
    public Collection<ItemDto> searchItemByTitle(@RequestParam String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemService.searchItemByTitle(text);
    }
}
