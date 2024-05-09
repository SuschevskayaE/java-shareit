package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.controller.dto.BookingItemResponse;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.controller.dto.ItemUserCommentsResponse;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemUserCommentsDtoTest {

    @Autowired
    private JacksonTester<ItemUserCommentsResponse> json;

    @Test
    void testItemUsersResponseDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingItemResponse lastBooking = new BookingItemResponse(2L, start, end, null, null, Status.WAITING);
        BookingItemResponse nextBooking = new BookingItemResponse(3L, start, end, null, null, Status.WAITING);

        CommentResponse commentResponse = new CommentResponse(
                4L,
                "text",
                null,
                "Author name",
                start
        );

        ItemUserCommentsResponse itemUserCommentsResponse = new ItemUserCommentsResponse(
                1L,
                "Item 1",
                "Item 1 des",
                true,
                lastBooking,
                nextBooking,
                Collections.singletonList(commentResponse)
        );


        JsonContent<ItemUserCommentsResponse> result = json.write(itemUserCommentsResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Item 1 des");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text");
    }
}
