package com.agendit.scheduler.tasks;
import com.agendit.scheduler.exception.WhatsAppMessageException;
import com.agendit.scheduler.httpclient.BackendHttpClient;
import com.agendit.scheduler.model.ReminderConfig;
import com.agendit.scheduler.model.response.AppointmentResponse;
import com.agendit.scheduler.model.response.Business;
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
import java.util.Locale;
import java.util.Optional;

public class WhatsAppReminder implements Tasklet {

    private final Logger log = LoggerFactory.getLogger(WhatsAppReminder.class);

    private BackendHttpClient backendHttpClient;

    private ReminderConfig reminderConfig;

    public WhatsAppReminder (ReminderConfig reminderConfig, BackendHttpClient backendHttpClient){
        this.backendHttpClient = backendHttpClient;
        this.reminderConfig = reminderConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception{
        log.atInfo().log("******Starting WhatsAppReminder******");
        try {
            var business = backendHttpClient.fetchBusiness(Optional.empty());

            log.atDebug().log("Business fetched: "+business.getBusinessId());

            if(!business.getBusinessConfig().getSendReminder() || !business.getBusinessConfig().getWhatsAppEnabled())
                return RepeatStatus.FINISHED;

            var appointments = backendHttpClient.fetchAppointments(business);

            for (var appointment :appointments) {
                try {
                    sendReminder(business, appointment);
                    backendHttpClient.updateAppointmentStatus(appointment);
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

    public void sendReminder(Business business, AppointmentResponse appointmentResponse) throws URISyntaxException, IOException, InterruptedException, WhatsAppMessageException {
        log.atInfo().log("Message sent to : "+ appointmentResponse.getCustomerPhone());

        var formattedStartDate = new SimpleDateFormat("EEEE, dd LLLL yyyy HH:mm", Locale.forLanguageTag("es-ES")).format(appointmentResponse.getAppointmentDateStart());

        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://graph.facebook.com/v17.0/"+reminderConfig.whatsAppMessageId+"/messages"))
                .header("Authorization", "Bearer "+reminderConfig.whatsAppAccessToken.trim())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\n" +
                        "    \"messaging_product\": \"whatsapp\",\n" +
                        "    \"to\": \""+ appointmentResponse.getCustomerPhone()+"\",\n" +
                        "    \"type\": \"template\",\n" +
                        "    \"template\": {\n" +
                        "        \"name\": \"appointment_reminder\",\n" +
                        "        \"language\": {\n" +
                        "            \"code\": \"es_AR\"\n" +
                        "        },\n" +
                        "       \"components\": [{\n" +
                                "            \"type\": \"header\",\n" +
                                "            \"parameters\" : [{                \n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+appointmentResponse.getCustomerName()+"\"                \n" +
                                "            }]\n" +
                                "        },        \n" +
                                "        {\n" +
                                "            \"type\": \"body\",\n" +
                                "            \"parameters\" : [\n" +
                                "               {\n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+appointmentResponse.getService().getServiceName()+"\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+business.getBusinessName()+"\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+appointmentResponse.getBranch().getBranchName()+"\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+appointmentResponse.getBranch().getAddress()+"\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+formattedStartDate+"\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"type\": \"text\",\n" +
                                "                    \"text\" : \""+appointmentResponse.getProfessional().getProfessionalName()+"\"\n" +
                                "                }\n" +
                                "            ]\n" +
                                "        }]"+
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