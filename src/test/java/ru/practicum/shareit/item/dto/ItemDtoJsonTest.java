package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoJsonTest {
    private final User user = User.builder()
            .name("User2")
            .email("user2@user.com")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item1")
            .description("item1")
            .available(true)
            .ownerId(user.getId())
            .build();

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testBookingDto() throws Exception {

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item1");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(user.getId());
    }

}