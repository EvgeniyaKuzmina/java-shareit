package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking creatNewBooking(Booking booking) {
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        return booking;

    }

    @Override
    public Booking processingBookingRequest(Long bookingId, Long ownerId, Boolean result) throws ValidationException {
        Booking booking = getBookingById(bookingId); //  получение бронирование по Id
        if (!booking.getItem().getOwner().getId().equals(ownerId)) { // проверяем что передан id владельца вещи
            log.error("BookingServiceImpl.processingBookingRequest: Указан неверный id {} владельца вещи", ownerId);
            throw new ValidationException("Указан неверный id владельца вещи");
        }
        if (result) {
            booking.setStatus(Status.APPROVED);
            return booking;
        }
        booking.setStatus(Status.REJECTED);
        return booking;
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) throws ValidationException {
        Booking booking = getBookingById(bookingId); //  получение бронирования по Id
        if (!booking.getItem().getOwner().getId().equals(userId) || !booking.getBooker().getId().equals(userId)) { // проверяем что передан id владельца вещи или id создателя бронирования
            log.error("BookingServiceImpl.processingBookingRequest: Указан неверный id {} владельца вещи", userId);
            throw new ValidationException("Указан неверный id владельца вещи");
        }
        return booking;
    }

    /* //Получение списка всех бронирований текущего пользователя.
Эндпоинт — GET /bookings?state={state}.
Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
Также он может принимать значения CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»), FUTURE (англ. «будущие»),
WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
Бронирования должны возвращаться отсортированными по дате от более новых к более старым.*/
    @Override
    public Collection<Booking> getBookingByBookerId(String state, Long bookerId) throws ObjectNotFountException, ValidationException {
        userService.getUserById(bookerId); // проверяем что пользователь с таким id существует
        Collection<Booking> bookings = getAllBookingByBookerId(bookerId);
        if (state == null) {
            return bookings;
        }
        switch (state) {
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(LocalDate.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDate.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(LocalDate.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(LocalDate.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDate.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatus().toString().equals("WAITING"))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatus().toString().equals("REJECTED"))
                        .collect(Collectors.toList());

            default:
                throw new ValidationException("Неверное значение у параметра state");


        }
       /* // сначала создаём описание сортировки по полю id
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "start");
        // затем создаём описание первой "страницы" размером 32 элемента
        Pageable page = PageRequest.of(0, 50, sortByDate);
        do {
            // запрашиваем у базы данных страницу с данными
            Page<User> userPage = repository.findAll(page);
            // результат запроса получаем с помощью метода getContent()
            userPage.getContent().forEach(user -> {
                // проверяем пользователей
            });
            // для типа Page проверяем, существует ли следующая страница
            if(userPage.hasNext()){
                // если следующая страница существует, создаём её описание, чтобы запросить на следующей итерации цикла
                page = PageRequest.of(userPage.getNumber() + 1, userPage.getSize(), userPage.getSort()); // или для простоты -- userPage.nextOrLastPageable()
            } else {
                page = null;
            }
        } while (page != null);*/
    }
    @Override
    public Collection<Booking> getBookingItemByOwnerId(String state, Long ownerId) throws ObjectNotFountException, ValidationException {
        userService.getUserById(ownerId); // проверяем что пользователь с таким id существует
        Collection<Item> items = itemService.getAllItemByUserId(ownerId);
        Collection<Booking> bookings = getAllBookingByBookerId(ownerId);
        if (state == null) {
            return bookings;
        }
        switch (state) {
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(LocalDate.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDate.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(LocalDate.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(LocalDate.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDate.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatus().toString().equals("WAITING"))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatus().toString().equals("REJECTED"))
                        .collect(Collectors.toList());

            default:
                throw new ValidationException("Неверное значение у параметра state");


        }
    }

    @Override
    public Booking getBookingById(Long id) throws ValidationException {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        bookingOpt.orElseThrow(() -> {
            log.error("ItemServiceImpl.getItemById: Бронирования с таким id нет ");
            return new ValidationException("Бронирования с таким id нет");
        });

        return bookingOpt.get();
    }

    @Override
    public Collection<Booking> getAllBookingByBookerId(Long id) {
        return bookingRepository.findAllByBookerIdOrderByStartDesc(id);

    }
}
