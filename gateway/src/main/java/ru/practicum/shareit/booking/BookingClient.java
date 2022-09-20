package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
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

    public ResponseEntity<Object> getBookingByBookerId(BookingState state, Long bookerId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state=" + state.name() + "&from=" + from + "&size=" + size, bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingItemByOwnerId(BookingState state, Long ownerId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );

        return get("?state=" + state.name() + "&from=" + from + "&size=" + size, ownerId, parameters);
    }
   /*
    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
*/


}
