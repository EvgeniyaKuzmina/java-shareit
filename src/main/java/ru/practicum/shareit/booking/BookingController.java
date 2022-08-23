package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * // контроллер для работы с бронированиями
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id пользователя

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final BookingService bookingService;

    // Добавление нового запроса на бронирование.
    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(HEADER_REQUEST) Long userId)
            throws ValidationException, ObjectNotFountException, ArgumentNotValidException {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ArgumentNotValidException("Дата начала бронирования не может быть позднее даты окончания бронирования");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ArgumentNotValidException("Дата начала бронирования не может быть ранее текущей даты");
        }
        Booking booking = bookingService.creatNewBooking(bookingDto, userId);
        return BookingMapper.toBookingDto(booking);
    }

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    @PatchMapping(value = {"/{bookingId}"})
    public BookingDto processingBooking(@PathVariable Long bookingId,
                                        @RequestParam Boolean approved,
                                        @RequestHeader(HEADER_REQUEST) Long ownerId) throws ObjectNotFountException, ValidationException, ArgumentNotValidException {
        Booking booking = bookingService.processingBookingRequest(bookingId, ownerId, approved);
        return BookingMapper.toBookingDto(booking);
    }

    //Получение данных о конкретном бронировании (включая его статус).
    @GetMapping(value = {"/{bookingId}"})
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(HEADER_REQUEST) Long ownerId) throws ValidationException, ObjectNotFountException {
        Booking booking = bookingService.getBookingById(bookingId, ownerId);
        return BookingMapper.toBookingDto(booking);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping
    public Collection<BookingDto> getBookingByBookerId(@RequestParam(required = false) String state,
                                                       @RequestHeader(HEADER_REQUEST) Long bookerId,
                                                       @RequestParam(required = false, defaultValue = FROM) @PositiveOrZero String from,
                                                       @RequestParam(required = false, defaultValue = SIZE) @Positive String size)

            throws ObjectNotFountException, ValidationException {

        Pageable pageable = PageRequest.of(Integer.parseInt(from), Integer.parseInt(size));
        Collection<Booking> bookings = bookingService.getBookingByBookerId(state, bookerId, pageable);
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(b -> bookingsDto.add(BookingMapper.toBookingDto(b)));
        return bookingsDto;
    }

    //Получение списка бронирований для всех вещей текущего пользователя. Эндпоинт GET /bookings/owner
    @GetMapping(path = "/owner")
    public Collection<BookingDto> getBookingItemByOwnerId(@RequestParam(required = false) String state,
                                                          @RequestHeader(HEADER_REQUEST) Long ownerId,
                                                          @RequestParam(required = false, defaultValue = FROM) @PositiveOrZero String from,
                                                          @RequestParam(required = false, defaultValue = SIZE) @Positive String size)
            throws ObjectNotFountException, ValidationException {

        Pageable pageable = PageRequest.of(Integer.parseInt(from), Integer.parseInt(size));
        Collection<Booking> bookings = bookingService.getBookingItemByOwnerId(state, ownerId, pageable);
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(b -> bookingsDto.add(BookingMapper.toBookingDto(b)));
        return bookingsDto;
    }

}
