package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
@Validated
public class ItemController {

    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id владельца вещи
    private static final String LAST = "LAST";
    private static final String NEXT = "NEXT";
    private static final String FROM = "0";
    private static final String SIZE = "10";
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
        ItemDto.LastOrNextBooking lastBooking = bookingService.getLastOrNextBookingForItem(item, userId, LAST);
        ItemDto.LastOrNextBooking nextBooking = bookingService.getLastOrNextBookingForItem(item, userId, NEXT);
        Collection<Comment> comments = bookingService.getAllCommentsByItemIdOrderByCreatDesc(item.getId());
        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    // обновление данных о вещи
    @PatchMapping(value = {"/{id}"})
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @PathVariable Long id,
                              @RequestHeader(HEADER_REQUEST) Long userId) throws ObjectNotFountException, ValidationException, ArgumentNotValidException {
        Item item = itemService.updateItem(itemDto, id, userId);
        ItemDto.LastOrNextBooking lastBooking = bookingService.getLastOrNextBookingForItem(item, userId, LAST);
        ItemDto.LastOrNextBooking nextBooking = bookingService.getLastOrNextBookingForItem(item, userId, NEXT);
        Collection<Comment> comments = bookingService.getAllCommentsByItemIdOrderByCreatDesc(item.getId());
        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
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
        Item item = itemService.getItemById(id);
        ItemDto.LastOrNextBooking lastBooking = bookingService.getLastOrNextBookingForItem(item, userId, LAST);
        ItemDto.LastOrNextBooking nextBooking = bookingService.getLastOrNextBookingForItem(item, userId, NEXT);
        Collection<Comment> comments = bookingService.getAllCommentsByItemIdOrderByCreatDesc(item.getId());
        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    // получение владельцем списка всех его вещей с комментариями. Эндпоинт GET items?from={from}&size={size}
    @GetMapping
    public Collection<ItemDto> getAllItemByUserId(@RequestHeader(HEADER_REQUEST) Long userId,
                                                  @RequestParam(required = false, defaultValue = FROM) @PositiveOrZero String from,
                                                  @RequestParam(required = false, defaultValue = SIZE) @Positive String size)
            throws ObjectNotFountException {

        int page = Integer.parseInt(from) / Integer.parseInt(size);
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size));
        Collection<ItemDto> itemsDto = new ArrayList<>();
        Collection<Item> items = itemService.getAllItemByUserId(userId, pageable);
        fillItemDto(items, itemsDto, userId);
        return itemsDto;
    }

    // поиск вещи по части строки в названии или в описании. Эндпоинт GET items/search??text={text}&&from={from}&size={size}
    @GetMapping("/search")
    public Collection<ItemDto> searchItemByNameOrDescription(@RequestParam String text,
                                                             @RequestHeader(HEADER_REQUEST) Long userId,
                                                             @RequestParam(required = false, defaultValue = FROM) @PositiveOrZero String from,
                                                             @RequestParam(required = false, defaultValue = SIZE) @Positive String size) {
        if (text.isEmpty()) {
            return List.of();
        }

        int page = Integer.parseInt(from) / Integer.parseInt(size);
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size));
        Collection<Item> items = itemService.searchItemByNameOrDescription(text, pageable);
        Collection<ItemDto> itemsDto = new ArrayList<>();
        fillItemDto(items, itemsDto, userId);
        return itemsDto;
    }

    // добавление комментария к вещи после бронирования
    @PostMapping(value = {"/{itemId}/comment"})
    public CommentDto addNewComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(HEADER_REQUEST) Long userId,
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

    private void fillItemDto(Collection<Item> items, Collection<ItemDto> itemsDto, Long userId) {
        items.forEach(i -> {
            ItemDto.LastOrNextBooking lastBooking = bookingService.getLastOrNextBookingForItem(i, userId, LAST);
            ItemDto.LastOrNextBooking nextBooking = bookingService.getLastOrNextBookingForItem(i, userId, NEXT);
            Collection<Comment> comments = bookingService.getAllCommentsByItemIdOrderByCreatDesc(i.getId());
            try {
                itemsDto.add(ItemMapper.toItemDto(i, lastBooking, nextBooking, comments));
            } catch (ValidationException | ObjectNotFountException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
