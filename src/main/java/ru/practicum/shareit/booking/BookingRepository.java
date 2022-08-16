package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    // получение всех бронирований пользователя отсортированные по дате создания в порядке убывания
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // получение всех бронирований пользователя отсортированные по дате создания в порядке возрастания
    Collection<Booking> findAllByBookerIdOrderByStartAsc(Long bookerId);

    // постраничное получение бронирований владельца вещей
    Page<Booking> findAllByItemIdOrderByStartDesc(Long itemId, Pageable pageable);

    //получение списка бронирований владельца вещей
    Collection<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

}
