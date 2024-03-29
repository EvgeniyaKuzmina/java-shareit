package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> creatBooking(BookingDto bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> processingBooking(Long bookingId, Boolean approved, Long ownerId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved=" + approved, ownerId, parameters);
    }

    //Получение данных о конкретном бронировании (включая его статус).
    public ResponseEntity<Object> getBookingById(Long bookingId, Long ownerId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );
        return get("/{bookingId}", ownerId, parameters);
    }

    public ResponseEntity<Object> getBookingByBookerId(String state, Long bookerId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        log.info("?state=" + state + "&from=" + from + "&size=" + size, bookerId, parameters);
        return get("?state=" + state + "&from=" + from + "&size=" + size, bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingItemByOwnerId(String state, Long ownerId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        log.info("/owner?state=" + state + "&from=" + from + "&size=" + size, ownerId, parameters);
        return get("/owner?state=" + state + "&from=" + from + "&size=" + size, ownerId, parameters);
    }

}
