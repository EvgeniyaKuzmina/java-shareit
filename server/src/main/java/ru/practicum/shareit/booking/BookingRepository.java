package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    // получение всех бронирований пользователя отсортированные по дате начала бронирования в порядке убывания
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // получение всех бронирований пользователя отсортированные по дате начала бронирования в порядке возрастания
    Collection<Booking> findAllByBookerIdOrderByStartAsc(Long bookerId);

    // постраничное получение бронирований владельца вещей
    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId, Pageable pageable);

    //получение списка бронирований владельца вещей
    Collection<Booking> findAllByItemIdOrderByStartDesc(Long itemId);




}
