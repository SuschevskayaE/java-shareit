package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.controller.dto.BookingItemResponse;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingItemDtoTest {

    @Autowired
    private JacksonTester<BookingItemResponse> json;

    @Test
    void testBookingItemResponseDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingItemResponse bookingItemResponse = new BookingItemResponse(4L,
                start,
                end,
                2L,
                3L,
                Status.WAITING
        );

        JsonContent<BookingItemResponse> result = json.write(bookingItemResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(3);
    }
}
