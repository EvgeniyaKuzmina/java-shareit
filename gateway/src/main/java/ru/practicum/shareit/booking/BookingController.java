package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ArgumentNotValidException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@RequestMapping(path = "/bookings")
@Slf4j
@RestController
@RequiredArgsConstructor
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
        log.info(bookingDto.toString());
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
    public ResponseEntity<Object> getBookingByBookerId(@RequestParam(required = false, defaultValue = STATE) String stateParam,
                                                       @RequestHeader(HEADER_REQUEST) Long bookerId,
                                                       @RequestParam(required = false, defaultValue = FROM) @PositiveOrZero String from,
                                                       @RequestParam(required = false, defaultValue = SIZE) @Positive String size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("gateway: BookingController: getBookingByBookerId():Get booking with state {}, bookerId={}, from={}, size={}", stateParam, bookerId, from, size);
        return bookingClient.getBookingByBookerId(state, bookerId, from, size);
    }

    //Получение списка бронирований для всех вещей текущего пользователя. Эндпоинт GET /bookings/owner
    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getBookingItemByOwnerId(@RequestParam(required = false, defaultValue = STATE) String stateParam,
                                                          @RequestHeader(HEADER_REQUEST) Long ownerId,
                                                          @RequestParam(required = false, defaultValue = FROM) @PositiveOrZero String from,
                                                          @RequestParam(required = false, defaultValue = SIZE) @Positive String size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("gateway: BookingController: getBookingItemByOwnerId(): Get booking with state {}, ownerId={}, from={}, size={}", state.name(), ownerId, from, size);
        return bookingClient.getBookingItemByOwnerId(state, ownerId, from, size);
    }

    //---------------
    // получение списка бронирований
	/*@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}
*/


}
