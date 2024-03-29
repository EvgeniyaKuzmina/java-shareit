package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * класс преобразовывающий сущность запроса вещи в Dto и обратно
 */
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<Item> items) {
        Collection<ItemRequestDto.Item> itemsForRequest = new ArrayList<>();
        items.forEach(i -> {
            ItemRequestDto.Item item = ItemRequestDto.Item.builder()
                    .id(i.getId())
                    .description(i.getDescription())
                    .name(i.getName())
                    .available(i.getAvailable())
                    .requestId(i.getItemRequest().getId())
                    .build();
            itemsForRequest.add(item);
        });
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(itemsForRequest)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .requester(user)
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();

    }
}
