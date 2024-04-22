package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingRepositoryMapper {


    Booking toBooking(BookingEntity entity);

    BookingEntity toEntity(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "start", ignore = true)
    @Mapping(target = "end", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    void updateEntity(Booking booking, @MappingTarget BookingEntity entity);
}
