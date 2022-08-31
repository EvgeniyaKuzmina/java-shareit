package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoJsonTest {
    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("new comment2")
            .authorName("user1")
            .creat(LocalDateTime.of(2023, 8, 19, 15, 0, 0))
            .build();
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testBookingDto() throws Exception {

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("new comment2");
        assertThat(result).extractingJsonPathStringValue("$.creat").isEqualTo(LocalDateTime.of(2023, 8, 19, 15, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}