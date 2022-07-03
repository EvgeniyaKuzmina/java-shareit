package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private static long firstId = 1;
    private final Map<Long, Item> items;

    @Override
    public ItemDto createItem(ItemDto itemDto, User user) {
        Long id;
        if (items.isEmpty()) {
            id = firstId;
        } else {
            id = nextId();
        }
        itemDto.setId(id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        items.put(id, item);
        log.info("ItemRepositoryImpl.createItem: Вещь создана, id вещи {} ", id);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long id) {
        Item updItem = items.get(id);
        if (itemDto.getName() != null) {
            updItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updItem.setAvailable(itemDto.getAvailable());
        }
        log.info("ItemRepositoryImpl.updateItem: Вещь c id {} обновлена", id);
        return ItemMapper.toItemDto(updItem);
    }

    @Override
    public void removeItem(Long id) {
        items.remove(id);
        log.info("ItemRepositoryImpl.removeItem: Вещь c id {} удалена", id);
    }

    @Override
    public Optional<ItemDto> getItemById(Long id) {
        log.info("ItemRepositoryImpl.getItemById: Вещь c id {} получена", id);
        return Optional.of(ItemMapper.toItemDto(items.get(id)));
    }

    @Override
    public Collection<ItemDto> getAllItem() {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        items.values().forEach(i -> itemsDto.add(ItemMapper.toItemDto(i)));
        log.info("ItemRepositoryImpl.getAllItem: Список всех вещей получен");
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> searchItemByTitle(String text) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        items.values().forEach(i -> {
            // проверяем что строка содержится в названии или описании, а статус вещи доступен к бронированию
            if (i.getName().toLowerCase().contains(text.toLowerCase()) && i.getAvailable() ||
                    i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable()) {
                itemsDto.add(ItemMapper.toItemDto(i));
            }
        });
        return itemsDto;
    }

    @Override
    public Map<Long, Item> getItems() {
        return items;
    }

    private Long nextId() {
        return ++firstId;
    }
}
