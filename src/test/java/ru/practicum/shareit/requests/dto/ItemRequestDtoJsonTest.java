package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    private final User user = User.builder()
            .name("User2")
            .email("user2@user.com")
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("запрос вещи")
            .requesterId(user.getId())
            .build();

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testBookingDto() throws Exception {

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("запрос вещи");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(user.getId());
    }

}