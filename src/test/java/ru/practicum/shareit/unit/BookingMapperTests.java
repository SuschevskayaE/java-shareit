package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.Booking;

import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTests {

    private BookingMapper bookingMapper;

    private BookingRepositoryMapper bookingRepositoryMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        bookingRepositoryMapper = Mappers.getMapper(BookingRepositoryMapper.class);
    }

    @Test
    void toBookingNull() {
        Booking item = bookingRepositoryMapper.toBooking(null);
        assertNull(item);
    }

    @Test
    void toEntityNull() {
        BookingEntity item = bookingRepositoryMapper.toEntity(null);
        assertNull(item);
    }
}
