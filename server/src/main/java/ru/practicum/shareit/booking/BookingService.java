package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface BookingService {


    // Добавление нового запроса на бронирование.
    Booking creatNewBooking(BookingDto bookingDto, Long userId);

    //Подтверждение или отклонение запроса на бронирование.
    Booking processingBookingRequest(Long bookingId, Long ownerId, Boolean result);

    //Получение данных о конкретном бронировании (включая его статус).
    Booking getBookingById(Long bookingId, Long userId);

    //Получение списка всех бронирований текущего пользователя.
    Collection<Booking> getBookingByBookerId(String state, Long bookerId, Pageable pageable);

    //Получение списка бронирований для всех вещей текущего пользователя.
    Collection<Booking> getBookingItemByOwnerId(String state, Long ownerId, Pageable pageable);

    // получение бронирования по Id
    Booking checkAndGetBookingById(Long id);

    // получение списка всех бронирований конкретного пользователя
    Collection<Booking> getAllBookingByBookerIdSortDesc(Long id, Pageable pageable);

    // получение всех бронирований по id создателя бронирования отсортированные в порядке возрастная id
    Collection<Booking> getAllBookingByBookerIdSortAsc(Long id);

    // получение последнего или следующего бронирования для указанной вещи
    ItemDto.LastOrNextBooking getLastOrNextBookingForItem(Item item, Long userId, String parameter);

    // получение всех комментарий вещи
    Collection<Comment> getAllCommentsByItemIdOrderByCreatDesc(Long itemId);

    // удаление бронирования
    void removeBooking(Long bookingId);

}
