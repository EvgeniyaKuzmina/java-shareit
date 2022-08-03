package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;

/**
 * класс преобразующий сущность вещи в Dto и обратно
 */

@Component
public class ItemMapper {


    private static UserServiceImpl userService;

    @Autowired
    public ItemMapper(UserServiceImpl userService) {
        ItemMapper.userService = userService;
    }


    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, Long userId) throws ObjectNotFountException {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(userService.getUserById(userId))
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItemFromBooking(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
