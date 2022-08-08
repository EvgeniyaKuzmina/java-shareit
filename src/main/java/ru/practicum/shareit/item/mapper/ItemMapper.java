package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.mapper.CommentMapper;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * класс преобразующий сущность вещи в Dto и обратно
 */

@Component
public class ItemMapper {
    private static final String LAST = "LAST";
    private static final String NEXT = "NEXT";

    private static BookingService bookingService;

    @Autowired
    public ItemMapper(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    public static ItemDto toItemDto(Item item, Long userId) throws ValidationException, ObjectNotFountException {
        ItemDto.LastOrNextBooking lastBooking = bookingService.getLastOrNextBookingForItem(item, userId, LAST);
        ItemDto.LastOrNextBooking nextBooking = bookingService.getLastOrNextBookingForItem(item, userId, NEXT);
        Collection<CommentDto> comments = new ArrayList<>();
        bookingService.findAllByItemIdOrderByCreatDesc(item.getId()).forEach(c -> comments.add(CommentMapper.toCommentDto(c)));
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable())
                .comments(comments)
                .build();
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }


    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(itemDto.getAvailable())
                .build();
    }

}
