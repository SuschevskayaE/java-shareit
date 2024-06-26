package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.controller.dto.BookingItemResponse;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemUserCommentsResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingItemResponse lastBooking;

    private BookingItemResponse nextBooking;

    private List<CommentResponse> comments;
}
