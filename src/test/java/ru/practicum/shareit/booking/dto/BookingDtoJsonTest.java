package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {
    private final User user = User.builder()
            .name("User2")
            .email("user2@user.com")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("Item1")
            .description("Item")
            .available(true)
            .owner(user)
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .itemId(item.getId())
            .start(LocalDateTime.of(2023, 8, 19, 15, 0, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0, 0))
            .build();
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(LocalDateTime.of(2023, 8, 19, 15, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(LocalDateTime.of(2023, 9, 19, 15, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

}