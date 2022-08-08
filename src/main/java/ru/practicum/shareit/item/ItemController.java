package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.mapper.CommentMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
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

    private final ItemService itemService;
    private final BookingService bookingService;

    // создание вещи
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ArgumentNotValidException, ObjectNotFountException, ValidationException {
        if (itemDto.getAvailable() == null || itemDto.getName() == null ||
                itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            log.warn("Не указано имя, описание товара или параметр доступности");
            throw new ArgumentNotValidException("Не указано имя, описание товара или параметр доступности");
        }
        Item item = itemService.createItem(itemDto, userId);
        return ItemMapper.toItemDto(item, userId);
    }

    // обновление данных о вещи
    @PatchMapping(value = {"/{id}"})
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @PathVariable Long id,
                              @RequestHeader(HEADER_REQUEST) Long userId) throws ObjectNotFountException, ValidationException {
        Item item = itemService.updateItem(itemDto, id, userId);
        return ItemMapper.toItemDto(item, userId);
    }

    //удаление вещи
    @DeleteMapping(value = {"/{id}"})
    public void removeItem(@PathVariable Long id, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ValidationException, ArgumentNotValidException, ObjectNotFountException {
        itemService.removeItem(id, userId);
    }

    // получение вещи по id с комментариями
    @GetMapping(value = {"/{id}"})
    public ItemDto getItemById(@PathVariable Long id, @RequestHeader(HEADER_REQUEST) Long userId) throws ObjectNotFountException, ValidationException {
        return ItemMapper.toItemDto(itemService.getItemById(id), userId);
    }

    // получение владельцем списка всех его вещей с комментариями
    @GetMapping
    public Collection<ItemDto> getAllItem(@RequestHeader(HEADER_REQUEST) Long userId)
            throws ObjectNotFountException, ValidationException {
        Collection<ItemDto> allItemDto = new ArrayList<>();
        itemService.getAllItemByUserId(userId).forEach(i -> {
            try {
                allItemDto.add(ItemMapper.toItemDto(i, userId));
            } catch (ValidationException | ObjectNotFountException e) {
                throw new RuntimeException(e);
            }
        });
        return allItemDto;
    }


    // поиск вещи по части строки в названии или в описании
    @GetMapping("/search")
    public Collection<ItemDto> searchItemByNameOrDescription(@RequestParam String text,
                                                             @RequestHeader(HEADER_REQUEST) Long userId) {
        if (text.isEmpty()) {
            return List.of();
        }
        Collection<ItemDto> itemsDtoIncludeText = new ArrayList<>();
        itemService.searchItemByNameOrDescription(text).forEach(i -> {
            try {
                itemsDtoIncludeText.add(ItemMapper.toItemDto(i, userId));
            } catch (ValidationException | ObjectNotFountException e) {
                throw new RuntimeException(e);
            }
        });
        return itemsDtoIncludeText;
    }

    // добавление комментария к вещи после бронирования
    @PostMapping(value = {"/{itemId}/comment"})
    public CommentDto addNewComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(HEADER_REQUEST) Long userId,
                                    @PathVariable Long itemId) throws ArgumentNotValidException, ValidationException, ObjectNotFountException {
        if (commentDto.getText().isEmpty()) {
            log.error("ItemController.addNewComment: Комментарий пустой");
            throw new ArgumentNotValidException("Нельзя оставить пустой комментарий");
        }
        // проверяем что пользователь действительно бронировал указанную вещь
        Collection<Booking> bookings = bookingService.getAllBookingByBookerIdSortAsc(userId);
        Comment comment = itemService.addNewComment(commentDto, userId, itemId, bookings);
        return CommentMapper.toCommentDto(comment);
    }
}
