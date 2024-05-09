package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.controller.dto.ItemResponse;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemResponse> json;

    @Test
    void testItemResponseDto() throws Exception {
        ItemResponse itemResponse = new ItemResponse(2L,
                "Item 1",
                "Item 1 des",
                true,
                3L);


        JsonContent<ItemResponse> result = json.write(itemResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Item 1 des");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
    }
}
