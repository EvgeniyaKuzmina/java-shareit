package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.Collection;

/**
 * // контроллер для работы с бронированиями
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id пользователя
    private final BookingService bookingService;

    // Добавление нового запроса на бронирование.
    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, @RequestHeader(HEADER_REQUEST) Long userId) {

        Booking booking = bookingService.creatNewBooking(bookingDto, userId);
        return BookingMapper.toBookingDto(booking);
    }

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    @PatchMapping(value = {"/{bookingId}"})
    public BookingDto processingBooking(@PathVariable Long bookingId,
                                        @RequestParam Boolean approved,
                                        @RequestHeader(HEADER_REQUEST) Long ownerId) {
        Booking booking = bookingService.processingBookingRequest(bookingId, ownerId, approved);
        return BookingMapper.toBookingDto(booking);
    }

    //Получение данных о конкретном бронировании (включая его статус).
    @GetMapping(value = {"/{bookingId}"})
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(HEADER_REQUEST) Long ownerId) {
        Booking booking = bookingService.getBookingById(bookingId, ownerId);
        return BookingMapper.toBookingDto(booking);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping
    public Collection<BookingDto> getBookingByBookerId(@RequestParam String state,
                                                       @RequestHeader(HEADER_REQUEST) Long bookerId,
                                                       @RequestParam String from,
                                                       @RequestParam String size) {
        int page = Integer.parseInt(from) / Integer.parseInt(size);
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size));
        Collection<Booking> bookings = bookingService.getBookingByBookerId(state, bookerId, pageable);
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(b -> bookingsDto.add(BookingMapper.toBookingDto(b)));
        return bookingsDto;
    }

    //Получение списка бронирований для всех вещей текущего пользователя. Эндпоинт GET /bookings/owner
    @GetMapping(path = "/owner")
    public Collection<BookingDto> getBookingItemByOwnerId(@RequestParam String state,
                                                          @RequestHeader(HEADER_REQUEST) Long ownerId,
                                                          @RequestParam String from,
                                                          @RequestParam String size) {
        int page = Integer.parseInt(from) / Integer.parseInt(size);
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size));
        Collection<Booking> bookings = bookingService.getBookingItemByOwnerId(state, ownerId, pageable);
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookings.forEach(b -> bookingsDto.add(BookingMapper.toBookingDto(b)));
        return bookingsDto;
    }

}
