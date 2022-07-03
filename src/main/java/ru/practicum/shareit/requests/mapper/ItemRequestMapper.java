package ru.practicum.shareit.requests.mapper;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

/**
 * класс преобразовывающий сущность запроса вещи в Dto и обратно
 */
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requester(itemRequestDto.getRequester())
                .created(itemRequestDto.getCreated())
                .build();
    }
}
