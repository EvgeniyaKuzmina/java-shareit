package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.CommentService;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.mapper.CommentMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentService commentService;

    @Override
    public Item createItem(ItemDto itemDto, Long id) throws ObjectNotFountException {
        User user = userService.getUserById(id); // проверяем что пользователь с таким id существует
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(ItemDto updItem, Long id, Long userId) throws ObjectNotFountException {
        userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = getItemById(id); // получаем вещь по Id
        if (!Objects.equals(
                item.getOwner().getId(), userId)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemServiceImpl.updateItem: Указан неверный id владельца вещи");
            throw new ObjectNotFountException("Указан неверный id  владельца вещи");
        }
        // обновление вещи
        Optional.ofNullable(updItem.getName()).ifPresent(item::setName);
        Optional.ofNullable(updItem.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(updItem.getAvailable()).ifPresent(item::setAvailable);

        return itemRepository.save(item);
    }

    @Override
    public void removeItem(Long id, Long userId) throws ArgumentNotValidException, ObjectNotFountException {
        Item item = getItemById(id); // получаем вещь по Id
        userService.getUserById(userId); // проверяем что пользователь с таким id существует
        if (!Objects.equals(item.getOwner().getId(), id)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemServiceImpl.removeItem: Указан неверный id владельца вещи");
            throw new ArgumentNotValidException("Указан неверный id  владельца вещи");
        }

        log.info("ItemServiceImpl.removeItem: Вещь с указанным id {} удалена", id);
        itemRepository.deleteById(id);
    }

    @Override
    public Item getItemById(Long id) throws ObjectNotFountException {
        Optional<Item> itemOpt = itemRepository.findById(id);
        itemOpt.orElseThrow(() -> {
            log.error("ItemServiceImpl.getItemById: Вещи с таким id нет ");
            return new ObjectNotFountException("Вещи с таким id нет");
        });

        return itemOpt.get();
    }

    @Override
    public Collection<Item> getAllItemByUserId(Long id) throws ObjectNotFountException {
        userService.getUserById(id); // проверяем что пользователь с таким id существует
        return itemRepository.findAllByOwnerId(id);
    }

    @Override
    public Collection<Item> searchItemByNameOrDescription(String text) {
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text, text).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Comment addNewComment(CommentDto commentDto, Long userId, Long itemId, Collection<Booking> bookings)
            throws ObjectNotFountException, ArgumentNotValidException {
        User user = userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = getItemById(itemId); // проверяем что вещь с таким id существует
        Comment comment = CommentMapper.toComment(commentDto, user, item);
        for (Booking b : bookings) {
            if (!b.getItem().getId().equals(itemId)) {
                log.error("ItemServiceImpl.addNewComment: Пользователь {} не делал бронирование указанной вещи", userId);
                throw new ArgumentNotValidException("Пользователь " + userId + " не делал бронирование указанной вещи");
            } else {
                if (b.getEnd().isBefore(LocalDateTime.now())) {
                    break;
                } else {
                    log.error("ItemServiceImpl.addNewComment: Нельзя оставить комментарий ранее даты бронирования");
                    throw new ArgumentNotValidException("Нельзя оставить комментарий ранее даты бронирования");
                }
            }
        }
        comment.setCreat(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);
        comment = commentService.saveComment(comment);
        return comment;
    }
}
