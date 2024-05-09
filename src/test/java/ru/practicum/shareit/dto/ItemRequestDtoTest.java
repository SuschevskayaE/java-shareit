package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestResponse> json;

    @Test
    void testItemRequestResponseDto() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        ItemResponse itemResponse = new ItemResponse(2L, "Item 1", "Item 1 des", true, 3L);
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(
                1L,
                "description 12",
                time,
                Collections.singletonList(itemResponse)
        );

        JsonContent<ItemRequestResponse> result = json.write(itemRequestResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description 12");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Item 1 des");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(3);
    }
}
