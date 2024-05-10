package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.controller.dto.ItemUsersResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemUsersDtoTest {

    @Autowired
    private JacksonTester<ItemUsersResponse> json;

    @Test
    void testItemUsersResponseDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking lastBooking = new Booking(2L, start, end, null, null, Status.WAITING);
        Booking nextBooking = new Booking(3L, start, end, null, null, Status.WAITING);

        ItemUsersResponse itemUsersResponse = new ItemUsersResponse(
                1L,
                "Item 1",
                "Item 1 des",
                true,
                lastBooking,
                nextBooking
        );


        JsonContent<ItemUsersResponse> result = json.write(itemUsersResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Item 1 des");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(3);
    }
}
