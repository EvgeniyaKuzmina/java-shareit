package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);
    Collection<Booking> findAllByBookerIdOrderByStartAsc(Long bookerId);

    Collection<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

}
