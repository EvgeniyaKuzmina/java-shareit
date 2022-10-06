package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String LAST = "LAST";
    private static final String NEXT = "NEXT";
    private final BookingRepository bookingRepository;
    private final CommentService commentService;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking creatNewBooking(BookingDto bookingDto, Long userId) {
        User user = userService.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = itemService.getItemById(bookingDto.getItemId()); // проверяем что вещь с таким id существует
        if (user.getId().equals(item.getOwner().getId())) { // проверяем что владелец вещи не бронирует свою собственную вещь
            log.error("BookingServiceImpl: creatNewBooking — Владелец вещи не может забронировать свою собственную вещь");
            throw new ObjectNotFountException("Владелец вещи не может забронировать свою собственную вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        if (!checkAvailable(item, booking)) {
            log.error("BookingServiceImpl: creatNewBooking — Указанная вещь на эти даты уже забронирована или недоступна к бронированию");
            throw new ArgumentNotValidException("Указанная вещь на эти даты уже забронирована или недоступна к бронированию");
        }
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking = bookingRepository.save(booking);
        return booking;

    }

    @Override
    public Booking processingBookingRequest(Long bookingId, Long ownerId, Boolean result) {
        userService.getUserById(ownerId); // проверяем что пользователь с таким id существует
        Booking booking = checkAndGetBookingById(bookingId); //  проверка и получение бронирование по Id
        if (!booking.getItem().getOwner().getId().equals(ownerId)) { // проверяем что передан id владельца вещи
            log.error("BookingServiceImpl.processingBookingRequest: Указан неверный id {} владельца вещи {}", ownerId, booking.getItem().getOwner().getId());
            throw new ObjectNotFountException("Указан неверный id владельца вещи");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            log.error("BookingServiceImpl.processingBookingRequest: Бронирование уже подтверждено. Нельзя подтвердить бронь дважды");
            throw new ArgumentNotValidException("Бронирование уже подтверждено. Нельзя подтвердить бронь дважды");
        }
        if (result) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = checkAndGetBookingById(bookingId); // проверка и получение бронирования по Id
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        if (!bookerId.equals(userId) && !itemOwnerId.equals(userId)) { // проверяем что передан id владельца вещи или id создателя бронирования
            log.error("BookingServiceImpl.getBookingById: Указан неверный id {} создателя бронирования или владельца вещи", userId);
            throw new ObjectNotFountException("Указан неверный id  " + userId + " создателя бронирования или владельца вещи");
        }
        return booking;
    }


    @Override
    public Collection<Booking> getBookingByBookerId(String state, Long bookerId, Pageable pageable) {
        userService.getUserById(bookerId); // проверяем что пользователь с таким id существует
        Collection<Booking> bookings = getAllBookingByBookerIdSortDesc(bookerId, pageable);
        return checkStateAndGetFilteredBookings(bookings, state);
    }

    @Override
    public Collection<Booking> getBookingItemByOwnerId(String state, Long ownerId, Pageable pageable) {
        userService.getUserById(ownerId); // проверяем что пользователь с таким id существует
        Collection<Item> items = itemService.getAllItemByUserIdWithoutPagination(ownerId); // получаем все вещи по указанному пользователю
        Collection<Booking> bookings = new ArrayList<>();
        items.forEach(i -> bookings.addAll(bookingRepository.findAllByItemIdOrderByStartDesc(i.getId(), pageable))); //  получаем все бронирования по каждой вещи пользователя
        return checkStateAndGetFilteredBookings(bookings, state);

    }

    @Override
    public Booking checkAndGetBookingById(Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        bookingOpt.orElseThrow(() -> {
            log.error("BookingServiceImpl.checkAndGetBookingById: Бронирования с таким id {} нет ", id);
            throw new ObjectNotFountException("Бронирования с таким id " + id + " нет");
        });
        return bookingOpt.get();
    }

    @Override
    public Collection<Booking> getAllBookingByBookerIdSortDesc(Long id, Pageable pageable) {
        return bookingRepository.findAllByBookerIdOrderByStartDesc(id, pageable);


    }

    @Override
    public Collection<Booking> getAllBookingByBookerIdSortAsc(Long id) {
        return bookingRepository.findAllByBookerIdOrderByStartAsc(id);
    }

    @Override
    public Collection<Comment> getAllCommentsByItemIdOrderByCreatDesc(Long itemId) {
        return commentService.findAllByItemIdOrderByCreatDesc(itemId);
    }

    @Override
    public ItemDto.LastOrNextBooking getLastOrNextBookingForItem(Item item, Long userId, String parameter) {
        if (!item.getOwner().getId().equals(userId)) {
            return null;
        }
        List<Booking> itemBookings = new ArrayList<>(bookingRepository.findAllByItemIdOrderByStartDesc(item.getId()));
        if (!itemBookings.isEmpty()) {
            switch (parameter) {
                case LAST:
                    LocalDateTime first = itemBookings.get(itemBookings.size() - 1).getStart(); // самое ранее бронирование вещи
                    return creatLastOrNextBooking(first, itemBookings, LAST);
                case NEXT:
                    LocalDateTime later = itemBookings.get(0).getStart(); // самое позднее бронирование вещи
                    return creatLastOrNextBooking(later, itemBookings, NEXT);
            }
        }
        return null;
    }

    @Override
    public void removeBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // проверка парамера запроса и возвращение отсортированных бронирований
    private Collection<Booking> checkStateAndGetFilteredBookings(Collection<Booking> bookings, String state) {
        Status status = Status.getStatus(state);
        switch (status) {
            case ALL:
                return bookings;
            case CURRENT:
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case PAST:
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case WAITING:
                return bookings.stream()
                        .filter(b -> b.getStatus().toString().equals(Status.WAITING.name()))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookings.stream()
                        .filter(b -> b.getStatus().toString().equals(Status.REJECTED.name()))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    // проверка доступности вещи на указанные даты бронирования
    private Boolean checkAvailable(Item item, Booking booking) {
        if (!item.getAvailable()) {
            return false;
        }
        List<Booking> itemBookings = new ArrayList<>(bookingRepository.findAllByItemIdOrderByStartDesc(item.getId()));
        for (Booking b : itemBookings) {
            if ((b.getStart().equals(booking.getStart()) || b.getStart().isAfter(booking.getStart())) &&
                    (b.getEnd().equals(booking.getEnd()) || b.getEnd().isBefore(booking.getEnd()))) {
                return false;
            }
        }
        return true;
    }


    private ItemDto.LastOrNextBooking creatLastOrNextBooking(LocalDateTime lastOrNext, List<Booking> itemBookings, String parameter) {
        for (Booking b : itemBookings) {
            switch (parameter) {
                case LAST: // обработка самого раннего бронирования
                    if ((b.getStart().isBefore(LocalDateTime.now()) || b.getStart().equals(LocalDateTime.now())) &&
                            (b.getStart().isAfter(lastOrNext) || b.getStart().equals(lastOrNext))) {
                        return ItemDto.LastOrNextBooking.builder()
                                .id(b.getId())
                                .bookerId(b.getBooker().getId())
                                .start(b.getStart())
                                .end(b.getEnd())
                                .build();
                    }
                case NEXT: // обработка самого позднего бронирования
                    if ((b.getStart().isAfter(LocalDateTime.now()) || b.getStart().equals(LocalDateTime.now())) &&
                            (b.getStart().isBefore(lastOrNext) || b.getStart().equals(lastOrNext))) {
                        return ItemDto.LastOrNextBooking.builder()
                                .id(b.getId())
                                .bookerId(b.getBooker().getId())
                                .start(b.getStart())
                                .end(b.getEnd())
                                .build();
                    }

            }
        }
        return null;
    }

}
