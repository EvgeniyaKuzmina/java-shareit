package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    private final Pageable pageable = PageRequest.of(0, 10);
    private User user = User.builder()
            .name("user")
            .email("user@user.ru")
            .build();

    private User user2 = User.builder()
            .name("User2")
            .email("user2@user.com")
            .build();

    private Item item = Item.builder()
            .name("Item1")
            .description("Item")
            .available(true)
            .owner(user)
            .build();
    private Booking booking = Booking.builder()
            .booker(user2)
            .item(item)
            .start(LocalDateTime.of(2023, 8, 19, 15, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0))
            .status(Status.WAITING)
            .build();
    private Booking pastBooking = Booking.builder()
            .booker(user2)
            .item(item)
            .start(LocalDateTime.of(2020, 8, 19, 15, 0))
            .end(LocalDateTime.of(2020, 9, 19, 15, 0))
            .status(Status.APPROVED)
            .build();
    private Booking pastBooking2 = Booking.builder()
            .booker(user)
            .item(item)
            .start(LocalDateTime.of(2021, 8, 19, 15, 0))
            .end(LocalDateTime.of(2021, 9, 19, 15, 0))
            .status(Status.WAITING)
            .build();
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);
        pastBooking = bookingRepository.save(pastBooking);
        pastBooking2 = bookingRepository.save(pastBooking2);
    }

    // получение всех бронирований пользователя отсортированные по дате начала бронирования в порядке убывания
    @Test
    void findAllByBookerIdOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId(), pageable);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(booking, pastBooking);
        assertThat(bookings.get(0)).isEqualTo(booking);
        assertThat(bookings.get(1)).isEqualTo(pastBooking);
    }

    // получение всех бронирований пользователя отсортированные по дате начала бронирования в порядке возрастания
    @Test
    void findAllByBookerIdOrderByStartAsc() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartAsc(user2.getId());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(booking, pastBooking);
        assertThat(bookings.toArray()[0]).isEqualTo(pastBooking);
        assertThat(bookings.toArray()[1]).isEqualTo(booking);
    }

    // проверка постраничного получение бронирований владельца вещей в  порядке убывания даты старта бронирования
    @Test
    void findAllByItemIdOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(item.getId(), pageable);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(3).contains(booking, pastBooking, pastBooking2);
        assertThat(bookings.toArray()[0]).isEqualTo(booking);
        assertThat(bookings.toArray()[1]).isEqualTo(pastBooking2);
        assertThat(bookings.toArray()[2]).isEqualTo(pastBooking);
    }

    // проверка постраничного получение бронирований владельца вещей в  порядке убывания даты старта бронирования
    @Test
    void findAllByItemIdOrderByStartDescWithPageFrom0To1() {
        final Pageable pageable = PageRequest.of(0, 1);
        Collection<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(item.getId(), pageable);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(booking);
        assertThat(bookings.toArray()[0]).isEqualTo(booking);
    }

    // проверка постраничного получение бронирований владельца вещей в  порядке убывания даты старта бронирования
    @Test
    void findAllByItemIdOrderByStartDescWithPageFrom0To2() {
        final Pageable pageable = PageRequest.of(0, 2);
        Collection<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(item.getId(), pageable);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(booking, pastBooking2);
        assertThat(bookings.toArray()[0]).isEqualTo(booking);
        assertThat(bookings.toArray()[1]).isEqualTo(pastBooking2);

    }

    //проверка получения списка бронирований владельца вещей в порядке убывания даты старта бронирования
    @Test
    void testFindAllByItemIdOrderByStartDesc() {
        Collection<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(item.getId());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(3).contains(booking, pastBooking, pastBooking2);
        assertThat(bookings.toArray()[0]).isEqualTo(booking);
        assertThat(bookings.toArray()[1]).isEqualTo(pastBooking2);
        assertThat(bookings.toArray()[2]).isEqualTo(pastBooking);
    }
}