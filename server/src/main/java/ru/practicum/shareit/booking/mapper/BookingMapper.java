package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingItemResponse;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(source = "itemId", target = "item.id")
    Booking toBooking(BookingCreateRequest request);

    BookingResponse toResponse(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "item.id", target = "itemId")
    BookingItemResponse toItemResponse(Booking booking);
}
