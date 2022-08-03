package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

public interface BookingService {

    /*
     // Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
     Эндпоинт — POST /bookings. После создания запрос находится в статусе WAITING — «ожидает подтверждения».*/

    Booking creatNewBooking(Booking booking);


    /*     //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    Затем статус бронирования становится либо APPROVED, либо REJECTED.
    Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать значения true или false.*/
    Booking processingBookingRequest(Long bookingId, Long ownerId, Boolean result) throws ValidationException;

/*  //Получение данных о конкретном бронировании (включая его статус).
Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
Эндпоинт — GET /bookings/{bookingId}.*/

    Booking getBookingById(Long bookingId, Long userId) throws ValidationException;


    /* //Получение списка всех бронирований текущего пользователя.
    Эндпоинт — GET /bookings?state={state}.
    Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
    Также он может принимать значения CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»), FUTURE (англ. «будущие»),
    WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
    Бронирования должны возвращаться отсортированными по дате от более новых к более старым.*/
    Collection<Booking> getBookingByBookerId(String state, Long bookerId) throws ValidationException, ObjectNotFountException;


    /* //Получение списка бронирований для всех вещей текущего пользователя.
    Эндпоинт — GET /bookings/owner?state={state}.
    Этот запрос имеет смысл для владельца хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем сценарии.*/
    Collection<Booking> getBookingItemByOwnerId(String state, Long ownerId) throws ObjectNotFountException, ValidationException;


    // получение бронирования по Id
    Booking getBookingById(Long id) throws ValidationException;

    // получение списка всех бронирований конкретного пользователя
    Collection<Booking> getAllBookingByBookerId(Long id) throws ObjectNotFountException, ValidationException;

}
