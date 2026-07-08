package com.thejaswini.flightbooking.mapper;

import com.thejaswini.flightbooking.dto.BookingResponse;
import com.thejaswini.flightbooking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper (compile-time generated, Spring-injectable) from the {@link Booking} domain
 * record to its {@link BookingResponse} API representation.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Maps a booking to its API response; the domain {@code id} becomes the response {@code bookingId}.
     *
     * @param booking the domain booking
     * @return the response DTO
     */
    @Mapping(target = "bookingId", source = "id")
    BookingResponse toResponse(Booking booking);
}
