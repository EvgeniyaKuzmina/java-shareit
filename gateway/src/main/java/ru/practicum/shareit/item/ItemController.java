package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * контроллер для работы с объектом вещь
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id владельца вещи
    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final ItemClient itemClient;

    // создание вещи
    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ArgumentNotValidException {
        if (itemDto.getAvailable() == null || itemDto.getName() == null ||
                itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            log.warn("gateway: ItemController.createItem: Не указано имя, описание товара или параметр доступности");
            throw new ArgumentNotValidException("Не указано имя, описание товара или параметр доступности");
        }

        return itemClient.createItem(itemDto, userId);
    }

    // обновление данных о вещи
    @PatchMapping(value = {"/{id}"})
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemDto itemDto,
                                             @PathVariable Long id,
                                             @RequestHeader(HEADER_REQUEST) Long userId) {
        return itemClient.updateItem(itemDto, id, userId);
    }

    //удаление вещи
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<Object> removeItem(@PathVariable Long id, @RequestHeader(HEADER_REQUEST) Long userId) {
        return itemClient.removeItem(id, userId);
    }

    // получение вещи по id с комментариями
    @GetMapping(value = {"/{id}"})
    public ResponseEntity<Object> getItemById(@PathVariable Long id, @RequestHeader(HEADER_REQUEST) Long userId) {
        return itemClient.getItemById(id, userId);
    }

    // получение владельцем списка всех его вещей с комментариями. Эндпоинт GET items?from={from}&size={size}
    @GetMapping
    public ResponseEntity<Object> getAllItemByUserId(@RequestHeader(HEADER_REQUEST) Long ownerId,
                                                     @RequestParam(defaultValue = FROM) @PositiveOrZero String from,
                                                     @RequestParam(defaultValue = SIZE) @Positive String size) {
        return itemClient.getAllItemByUserId(ownerId, from, size);
    }

    // поиск вещи по части строки в названии или в описании. Эндпоинт GET items/search??text={text}&&from={from}&size={size}
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByNameOrDescription(@RequestParam String text,
                                                                @RequestHeader(HEADER_REQUEST) Long userId,
                                                                @RequestParam(defaultValue = FROM) @PositiveOrZero String from,
                                                                @RequestParam(defaultValue = SIZE) @Positive String size) {
        return itemClient.searchItemByNameOrDescription(text, userId, from, size);
    }

    // добавление комментария к вещи после бронирования
    @PostMapping(value = {"/{itemId}/comment"})
    public ResponseEntity<Object> addNewComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(HEADER_REQUEST) Long userId,
                                                @PathVariable Long itemId) throws ArgumentNotValidException {
        if (commentDto.getText().isEmpty()) {
            log.warn("gateway: ItemController.addNewComment: Комментарий пустой");
            throw new ArgumentNotValidException("Нельзя оставить пустой комментарий");
        }
        return itemClient.addNewComment(commentDto, userId, itemId);
    }

}
