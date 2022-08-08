package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface BookingService {


    // Добавление нового запроса на бронирование.
    Booking creatNewBooking(BookingDto bookingDto, Long userId) throws ObjectNotFountException, ValidationException, ArgumentNotValidException;

    //Подтверждение или отклонение запроса на бронирование.
    Booking processingBookingRequest(Long bookingId, Long ownerId, Boolean result) throws ValidationException, ObjectNotFountException, ArgumentNotValidException;

    //Получение данных о конкретном бронировании (включая его статус).
    Booking getBookingById(Long bookingId, Long userId) throws ValidationException, ObjectNotFountException;


    //Получение списка всех бронирований текущего пользователя.
    Collection<Booking> getBookingByBookerId(String state, Long bookerId) throws ValidationException, ObjectNotFountException;


    //Получение списка бронирований для всех вещей текущего пользователя.
    Collection<Booking> getBookingItemByOwnerId(String state, Long ownerId) throws ObjectNotFountException, ValidationException;


    // получение бронирования по Id
    Booking checkAndGetBookingById(Long id) throws ValidationException, ObjectNotFountException;

    // получение списка всех бронирований конкретного пользователя
    Collection<Booking> getAllBookingByBookerIdSortDesc(Long id) throws ObjectNotFountException, ValidationException;

    Collection<Booking> getAllBookingByBookerIdSortAsc(Long id);

    // получение последнего или следующего бронирования для указанной вещи
    ItemDto.LastOrNextBooking getLastOrNextBookingForItem(Item item, Long userId, String parameter);

    // получение всех комментарий вещи
    Collection<Comment> findAllByItemIdOrderByCreatDesc(Long itemId);

}
