package com.agendit.scheduler.mapper;

import com.agendit.scheduler.model.request.AppointmentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    @Mapping(target = "serviceId", source = "service.serviceId")
    @Mapping(target = "professionalId", source = "professional.professionalId")
    AppointmentRequest appointmentResponseToAppointmentRequest(com.agendit.scheduler.model.response.AppointmentResponse appointment);

}
