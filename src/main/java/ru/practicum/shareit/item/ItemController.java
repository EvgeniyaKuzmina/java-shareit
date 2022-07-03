package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * контроллер для работы с объектом вещь
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) throws ValidationException, ArgumentNotValidException {
        if (userId == null) {
            log.warn("Не указан id владельца вещи");
            throw new ArgumentNotValidException("Не указан id владельца вещи");
        }
        return itemService.createItem(itemDto, userId);
    }

    @PutMapping
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) throws ArgumentNotValidException, ValidationException {
        if (userId == null) {
            log.warn("Не указан id владельца вещи");
            throw new ArgumentNotValidException("Не указан id владельца вещи");
        }
        return itemService.updateItem(itemDto, userId);
    }

    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public void removeItem(@PathVariable Long id) throws ValidationException {
        itemService.removeItem(id);

    }

    @GetMapping(value = {"/{id}"})
    @ResponseBody
    public ItemDto getItemById(@PathVariable Long id) throws ValidationException {
        return itemService.getItemById(id);
    }

    @GetMapping
    @ResponseBody
    public Collection<ItemDto> getAllItem(@RequestHeader("X-Sharer-User-Id") Long userId) throws ValidationException, ArgumentNotValidException {
        if (userId == null) {
            log.warn("Не указан id владельца вещи");
            throw new ArgumentNotValidException("Не указан id владельца вещи");
        }
        return itemService.getAllItem(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ItemDto> searchItemByTitle(@RequestParam String text) {
        return itemService.searchItemByTitle(text);

    }
}
