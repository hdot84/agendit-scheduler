package com.agendit.scheduler.tasks;
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
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;



public class WhatsAppReminder implements Tasklet {

    private final MapStructMapper mapStructMapper;

    private static final Logger log = LoggerFactory.getLogger(WhatsAppReminder.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    public WhatsAppReminder (MapStructMapper mapStructMapper){
        this.mapStructMapper = mapStructMapper;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception{
        log.atInfo().log("******Starting WhatsAppReminder******");
        try {
            var business = fetchBusiness(1);
            log.atDebug().log("Business fetched: "+business.getBusinessId());
            if(!business.getBusinessConfig().getSendReminder())
                return RepeatStatus.FINISHED;

            var appointments = fetchAppointments(business);

            for (var appointment :appointments) {
                try {
                    sendReminder(appointment);
                    updateAppointmentStatus(appointment);
                } catch (IllegalArgumentException | WhatsAppMessageException e){
                    log.atError().log(e.getLocalizedMessage());
                }
            }


        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.atError().log(e.getLocalizedMessage());
        }

        Thread.sleep(Duration.ofMillis(10).toMillis());
        log.atInfo().log("******Finishing WhatsAppReminder******");
        return RepeatStatus.FINISHED;
    }

    private Business fetchBusiness(long businessId) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(
                        URI.create("http://localhost:8080/api/v1/businesses/"+businessId))
                .header("accept", "application/json")
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), Business.class);
    }

    private List<AppointmentResponse> fetchAppointments(Business business) throws IOException, InterruptedException, URISyntaxException {
        var currentTimeNow = Calendar.getInstance();
        var currentTimeAhead = Calendar.getInstance();

        currentTimeAhead.add(Calendar.MINUTE, business.getBusinessConfig().getReminderBeforeTime());

        var formattedStartDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentTimeNow.getTime()).replace(" ", "%20");
        var formattedEndDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentTimeAhead.getTime()).replace(" ", "%20");

        var request = HttpRequest.newBuilder(
                        URI.create("http://localhost:8080/api/v1/appointments/between?businessId="+business.getBusinessId()+"&startDate="+formattedStartDate+"&endDate="+formattedEndDate+"&state="+AppointmentState.Reserved.toString()))
                .header("accept", "application/json")
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), new TypeReference<List<AppointmentResponse>>(){});
    }

    private void updateAppointmentStatus(AppointmentResponse appointmentResponse) throws IOException, InterruptedException, URISyntaxException, WhatsAppMessageException {
        log.atDebug().log("Updating appointment: "+appointmentResponse.getAppointmentId());

        AppointmentRequest appointmentRequest = mapStructMapper.appointmentResponseToAppointmentRequest(appointmentResponse);
        appointmentRequest.setState(AppointmentState.ReminderSent);

        var request = HttpRequest.newBuilder(
                        URI.create("http://localhost:8080/api/v1/appointments"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(appointmentRequest)))
                .build();

        var client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200){
            throw new IllegalArgumentException("The appointment wasn't updated: "+response.body());
        }
    }

    private void sendReminder(AppointmentResponse appointmentResponse) throws URISyntaxException, IOException, InterruptedException, WhatsAppMessageException {
        log.atInfo().log("Message sent to : "+ appointmentResponse.getCustomerPhone());
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://graph.facebook.com/v17.0/126798833852714/messages"))
                .header("Authorization", "Bearer EAALrrKwER5UBOyPOECZCsE1h57cjZCOqOQ26mogfCIJaB8FIoWwUlJzCcZAD3FBa2Uq9kc8TWDCv7jeV07MZAL8crFXJv6ouQK7XHNGs16dNy761b4W2VydGtRVhS9K4keCOjHUulhIKO3JmDg2K57sW1ZCqq1ZCwicZAxudj2j4bZCBpZBOHVlcS7SGQmgjqhrz9VjdtJweTcFssJ8KNrsD07BVRBu86Mh3sLBAZD")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("" +
                        "{\n" +
                        "    \"messaging_product\": \"whatsapp\",\n" +
                        "    \"to\": \""+ appointmentResponse.getCustomerPhone()+"\",\n" +
                        "    \"type\": \"template\",\n" +
                        "    \"template\": {\n" +
                        "        \"name\": \"appointment_reminder\",\n" +
                        "        \"language\": {\n" +
                        "            \"code\": \"es_AR\"\n" +
                        "        },\n" +
                        "        \"components\": [{\n" +
                        "            \"type\" : \"header\",\n" +
                        "            \"parameters\" : [{\n" +
                        "                \"type\" : \"text\",\n" +
                        "                \"text\" : \""+ appointmentResponse.getCustomerName()+"\"\n" +
                        "            }]\n" +
                        "        }]\n" +
                        "    }\n" +
                        "}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200){
            throw new WhatsAppMessageException("Message wasn't sent: "+response.body());
        }
    }
}