package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@RequestMapping(path = "/bookings")
@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id пользователя
    private static final String FROM = "0";
    private static final String SIZE = "10";
    private static final String STATE = "ALL";
    private final BookingClient bookingClient;

    // Добавление нового запроса на бронирование.
    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                @RequestHeader(HEADER_REQUEST) Long userId)
            throws ArgumentNotValidException {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ArgumentNotValidException("Дата начала бронирования не может быть позднее даты окончания бронирования");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ArgumentNotValidException("Дата начала бронирования не может быть ранее текущей даты");
        }
        return bookingClient.creatBooking(bookingDto, userId);
    }

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    @PatchMapping(value = {"/{bookingId}"})
    public ResponseEntity<Object> processingBooking(@PathVariable Long bookingId,
                                                    @RequestParam Boolean approved,
                                                    @RequestHeader(HEADER_REQUEST) Long ownerId) {
        return bookingClient.processingBooking(bookingId, approved, ownerId);
    }

    //Получение данных о конкретном бронировании (включая его статус).
    @GetMapping(value = {"/{bookingId}"})
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader(HEADER_REQUEST) Long ownerId) {
        return bookingClient.getBookingById(bookingId, ownerId);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping
    public ResponseEntity<Object> getBookingByBookerId(@RequestParam(defaultValue = STATE) String state,
                                                       @RequestHeader(HEADER_REQUEST) Long bookerId,
                                                       @RequestParam(defaultValue = FROM) @PositiveOrZero String from,
                                                       @RequestParam(defaultValue = SIZE) @Positive String size) throws ValidationException {
        BookingStatus status = BookingStatus.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("gateway: BookingController: getBookingByBookerId():Get booking with state {}, bookerId={}, from={}, size={}", state, bookerId, from, size);
        return bookingClient.getBookingByBookerId(status, bookerId, from, size);
    }

    //Получение списка бронирований для всех вещей текущего пользователя. Эндпоинт GET /bookings/owner
    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getBookingItemByOwnerId(@RequestParam(defaultValue = STATE) String state,
                                                          @RequestHeader(HEADER_REQUEST) Long ownerId,
                                                          @RequestParam(defaultValue = FROM) @PositiveOrZero String from,
                                                          @RequestParam(defaultValue = SIZE) @Positive String size) throws ValidationException {
        BookingStatus status = BookingStatus.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("gateway: BookingController: getBookingItemByOwnerId(): Get booking with state {}, ownerId={}, from={}, size={}", status.name(), ownerId, from, size);
        return bookingClient.getBookingItemByOwnerId(status, ownerId, from, size);
    }

}
