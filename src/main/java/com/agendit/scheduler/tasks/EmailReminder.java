package com.agendit.scheduler.tasks;
import com.agendit.scheduler.exception.WhatsAppMessageException;
import com.agendit.scheduler.httpclient.BackendHttpClient;
import com.agendit.scheduler.model.ReminderConfig;
import com.agendit.scheduler.model.response.AppointmentResponse;
import com.agendit.scheduler.model.response.Business;
import com.agendit.scheduler.service.EmailService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class EmailReminder implements Tasklet {

    private final Logger log = LoggerFactory.getLogger(EmailReminder.class);

    private BackendHttpClient backendHttpClient;

    private EmailService emailService;

    private ReminderConfig reminderConfig;

    public EmailReminder (EmailService emailService, ReminderConfig reminderConfig, BackendHttpClient backendHttpClient){
        this.emailService = emailService;
        this.backendHttpClient = backendHttpClient;
        this.reminderConfig = reminderConfig;
    }

    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception{
        log.atInfo().log("******Starting EmailReminder******");
        try {
            //Temporary until released for all businesses.
            var business = backendHttpClient.fetchBusiness(Optional.empty());

            log.atDebug().log("Business fetched: "+business.getBusinessId());

            if(!business.getBusinessConfig().getSendReminder() || !business.getBusinessConfig().getEmailEnabled())
                return RepeatStatus.FINISHED;

            var appointments = backendHttpClient.fetchAppointments(business);

            for (var appointment :appointments) {
                try {
                    sendReminder(business, appointment);
                    backendHttpClient.updateAppointmentStatus(appointment);
                } catch (URISyntaxException | IOException | InterruptedException | MessagingException e){
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

    public void sendReminder(Business business, AppointmentResponse appointment) throws URISyntaxException, IOException, InterruptedException, MessagingException {
        Map<String, Object> model = new HashMap<>();
        var confirmBookingUrl = "https://www.agendit.com.py/business/"+appointment.getBusinessId()+"/branch/"+appointment.getBranch().getBranchId()+"/booking";
        var cancelBookingUrl = "https://www.agendit.com.py/business/"+appointment.getBusinessId()+"/branch/"+appointment.getBranch().getBranchId()+"/booking";

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy HH:mm", Locale.of("es", "ES"));
        formatter.setTimeZone(TimeZone.getTimeZone("America/Asuncion"));

        String startDate = formatter.format(appointment.getAppointmentDateStart());
        String firstName = appointment.getCustomerName().split(" ")[0];

        model.put("customerName", firstName);
        model.put("appointmentDateStart", startDate);
        model.put("serviceName", appointment.getService().getServiceName());
        model.put("serviceDuration", appointment.getService().getDuration());
        model.put("branchName", appointment.getBranch().getBranchName());
        model.put("branchAddress", appointment.getBranch().getAddress());
        model.put("branchTelephone", appointment.getBranch().getTelephone());
        model.put("professionalName", appointment.getProfessional().getProfessionalName());
        model.put("confirmBookingUrl", confirmBookingUrl);
        model.put("cancelBookingUrl", cancelBookingUrl);
        model.put("footer", "El equipo de "+business.getBusinessName()+" ;)");

        emailService.sendMessageUsingThymeleafTemplate(appointment.getCustomerEmail(),firstName+", favor confirme su asistencia en "+business.getBusinessName(), model);
    }
}