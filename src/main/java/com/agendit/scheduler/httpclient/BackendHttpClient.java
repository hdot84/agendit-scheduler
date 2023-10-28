package com.agendit.scheduler.httpclient;

import com.agendit.scheduler.exception.WhatsAppMessageException;
import com.agendit.scheduler.mapper.MapStructMapper;
import com.agendit.scheduler.model.request.AppointmentRequest;
import com.agendit.scheduler.model.request.AppointmentState;
import com.agendit.scheduler.model.response.AppointmentResponse;
import com.agendit.scheduler.model.response.Business;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Component
public class BackendHttpClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger log = LoggerFactory.getLogger(BackendHttpClient.class);

    @Autowired
    private MapStructMapper mapStructMapper;

    @Value("${default.business.id}")
    Long defaultBusinessId;

    @Value("${backend.api.url}")
    private String backendApiUrl;

    public Business fetchBusiness(Optional<Long> businessId) throws IOException, InterruptedException {
        var id = businessId.isPresent() ? businessId : defaultBusinessId;
        var request = HttpRequest.newBuilder(
                        URI.create(backendApiUrl+"/businesses/"+ id ))
                .header("accept", "application/json")
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), Business.class);
    }

    public List<AppointmentResponse> fetchAppointments(Business business) throws IOException, InterruptedException, URISyntaxException {
        var currentTimeNow = Calendar.getInstance();
        var currentTimeAhead = Calendar.getInstance();

        currentTimeAhead.add(Calendar.MINUTE, business.getBusinessConfig().getReminderBeforeTime());

        var formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Asuncion"));

        var formattedStartDate = formatter.format(currentTimeNow.getTime()).replace(" ", "%20");
        var formattedEndDate = formatter.format(currentTimeAhead.getTime()).replace(" ", "%20");

        var request = HttpRequest.newBuilder(
                        URI.create(backendApiUrl+"/appointments/between?businessId=" + business.getBusinessId() + "&startDate=" + formattedStartDate + "&endDate=" + formattedEndDate + "&state=" + AppointmentState.Reserved.toString()))
                .header("accept", "application/json")
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), new TypeReference<List<AppointmentResponse>>() {
        });
    }

    public void updateAppointmentStatus(AppointmentResponse appointmentResponse) throws IOException, InterruptedException, URISyntaxException, WhatsAppMessageException {
        log.atDebug().log("Updating appointment: " + appointmentResponse.getAppointmentId());

        AppointmentRequest appointmentRequest = mapStructMapper.appointmentResponseToAppointmentRequest(appointmentResponse);
        appointmentRequest.setState(AppointmentState.ReminderSent);

        var request = HttpRequest.newBuilder(
                        URI.create(backendApiUrl+"/appointments"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(appointmentRequest)))
                .build();

        var client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalArgumentException("The appointment wasn't updated: " + response.body());
        }
    }
}
