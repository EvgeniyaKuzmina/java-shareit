package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentService commentService;
    private final RequestService requestService;

    @Override
    public Item createItem(ItemDto itemDto, Long userId) throws ObjectNotFountException, ArgumentNotValidException {
        User user = userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestService.getRequestById(itemDto.getRequestId(), userId); // проверяем что указанный запрос существует и получаем его
            item.setItemRequest(itemRequest);
        } else {
            item = ItemMapper.toItem(itemDto, user);
        }
        try {
            item = itemRepository.save(item);
        } catch (Throwable e) {
            log.error("Данная вещь уже существует");
            throw new ArgumentNotValidException("Данная вещь уже существует");
        }
        return item;
    }

    @Override
    public Item updateItem(ItemDto updItem, Long id, Long userId) throws ObjectNotFountException, ArgumentNotValidException {
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
        try {
            item = itemRepository.save(item);
        } catch (Throwable e) {
            log.error("Данная вещь уже существует");
            throw new ArgumentNotValidException("Данная вещь уже существует");
        }

        return item;
    }

    @Override
    public void removeItem(Long id, Long userId) throws ArgumentNotValidException, ObjectNotFountException {
        Item item = getItemById(id); // получаем вещь по Id
        userService.getUserById(userId); // проверяем что пользователь с таким id существует
        if (!Objects.equals(item.getOwner().getId(), userId)) { // проверяем что передан id владельца в заголовке
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
    public Collection<Item> getAllItemByUserId(Long id, Pageable pageable) throws ObjectNotFountException {
        userService.getUserById(id); // проверяем что пользователь с таким id существует
        return itemRepository.findAllByOwnerIdOrderByIdAsc(id, pageable);
    }

    @Override
    public Collection<Item> getAllItemByUserIdWithoutPagination(Long id) throws ObjectNotFountException {
        userService.getUserById(id); // проверяем что пользователь с таким id существует
        return itemRepository.findAllByOwnerIdOrderByIdAsc(id);
    }

    @Override
    public Collection<Item> searchItemByNameOrDescription(String text, Pageable pageable) {
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, pageable);
    }

    @Override
    public Comment addNewComment(CommentDto commentDto, Long userId, Long itemId, Collection<Booking> bookings)
            throws ObjectNotFountException, ArgumentNotValidException {
        User user = userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = getItemById(itemId); // проверяем что вещь с таким id существует
        Comment comment = CommentMapper.toComment(commentDto, user, item);
        if (bookings.isEmpty()) {
            log.error("ItemServiceImpl.addNewComment: Пользователь {} не делал бронирование указанной вещи", userId);
            throw new ArgumentNotValidException("Пользователь " + userId + " не делал бронирование указанной вещи");
        }
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
        comment = commentService.addNewComment(comment);
        return comment;
    }

    @Override
    public Collection<Item> findAllByRequestId(Long requestId) {
        return itemRepository.findAllByItemRequestId(requestId);
    }
}
