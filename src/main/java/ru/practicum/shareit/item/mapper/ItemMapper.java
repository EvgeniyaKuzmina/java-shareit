package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.mapper.CommentMapper;
import ru.practicum.shareit.booking.model.Comment;
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

    public static ItemDto toItemDto(Item item, ItemDto.LastOrNextBooking lastBooking,
                                          ItemDto.LastOrNextBooking nextBooking, Collection<Comment> comments)
            throws ValidationException, ObjectNotFountException {
        Collection<CommentDto> commentsDto = new ArrayList<>();
        comments.forEach(c -> commentsDto.add(CommentMapper.toCommentDto(c)));
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable())
                .comments(commentsDto)
                .build();
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
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
