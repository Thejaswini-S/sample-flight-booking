package com.thejaswini.flightbooking.mapper;

import com.thejaswini.flightbooking.dto.FlightResponse;
import com.thejaswini.flightbooking.model.Flight;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper (compile-time generated, Spring-injectable) from the {@link Flight} domain
 * object to its {@link FlightResponse} API representation. All fields map by name.
 */
@Mapper(componentModel = "spring")
public interface FlightMapper {

    /**
     * Maps a flight to its API response.
     *
     * @param flight the domain flight
     * @return the response DTO
     */
    FlightResponse toResponse(Flight flight);
}
