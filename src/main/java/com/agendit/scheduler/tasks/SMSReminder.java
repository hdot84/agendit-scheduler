package com.agendit.scheduler.tasks;

import com.agendit.scheduler.exception.WhatsAppMessageException;
import com.agendit.scheduler.model.request.PhoneVerificationRequest;
import com.agendit.scheduler.model.response.AppointmentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import com.telesign.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class SMSReminder implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppReminder.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${api.key}")
    private String apiKey;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try{
            String customerId = System.getenv().getOrDefault("CUSTOMER_ID", "A1A304F1-4803-405B-A0D4-C6387D27677D");
            String apiKey = System.getenv().getOrDefault("API_KEY", "3O9zGT2bD2OvaK75Z+bMYhpbLk5OepSrexJRMLM2qlvXclhQ+gdb5JNXj2mNIfM8Z8dFchpo5ERhluBUTJMVew==");
            String phoneNumber = System.getenv().getOrDefault("PHONE_NUMBER", "595972511222");

            sendMessage(customerId, apiKey, phoneNumber);

        } catch (Exception e) {
            System.out.println((char)27 + "[31m" + "\nAn exception occurred.\nERROR: " + e.getMessage());
        }
        return RepeatStatus.FINISHED;
    }

    private void sendMessage(String customerId, String apiKey, String phoneNumber) throws GeneralSecurityException, IOException {
        String message = "Your package has shipped! Follow your delivery at https://vero-finto.com/orders/3456";
        String messageType = "ARN";

        MessagingClient messagingClient = new MessagingClient(customerId, apiKey);
        RestClient.TelesignResponse telesignResponse = messagingClient.message(phoneNumber, message, messageType, null);
        if (telesignResponse.statusCode != 200) {
            throw new IllegalArgumentException("The code wasn't sent: " + telesignResponse.body);
        }
    }


    private void sendOTP(String customerId, String apiKey) throws IOException, InterruptedException {
        // Replace the defaults below with your Telesign authentication credentials or pull them from environment variables.

        String key = customerId+":"+apiKey;

        key = new String(key.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        key =  Base64.getEncoder().encodeToString(key.getBytes());
        key = new String(key.getBytes(), StandardCharsets.UTF_8);


        // Set the default below to your test phone number or pull it from an environment variable.
        // In your production code, update the phone number dynamically for each transaction.
        String phoneNumber = System.getenv().getOrDefault("PHONE_NUMBER", "595972511222");

        // Generate one-time passcode (OTP) and add it to request parameters.
        String verifyCode = Util.randomWithNDigits(5);
        HashMap<String, String> params = new HashMap<>();
        params.put("verify_code", verifyCode);


        PhoneVerificationRequest phoneVerificationRequest = new PhoneVerificationRequest();
        phoneVerificationRequest.setPhoneNumber(phoneNumber);
        phoneVerificationRequest.setVerifyCode(verifyCode);

        var request = HttpRequest.newBuilder(
                        URI.create("https://rest-ww.telesign.com/v1/verify/sms"))
                .header("authorization", "Basic " + key)
                .setHeader("accept", "application/json")
                .header("content-Type", "application/x-www-form-urlencoded")
                .header("date", new Date().toString())
                .POST(HttpRequest.BodyPublishers.ofString(String.format("is_primary=true&phone_number=%s", phoneNumber)))
                .build();

        var client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalArgumentException("The code wasn't sent: " + response.body());
        }
        // Display the response body in the console for debugging purposes.
        // In your production code, you would likely remove this.
        System.out.println("\n" + "Response HTTP status:" + response.statusCode());
        System.out.println("Response body:" + response.body() + "\n");

        // Display prompt to enter asserted OTP in the console.
        // In your production code, you would instead collect the asserted OTP from the end-user.
        System.out.println("Please enter the verification code you were sent:");
        Scanner s = new Scanner(System.in);
        String code = s.next();

        // Determine if the asserted OTP matches your original OTP, and resolve the login attempt accordingly.
        // You can simulate this by reporting whether the codes match.
        if (verifyCode.equalsIgnoreCase(code)) {
            System.out.println("Your code is correct.");
        } else {
            System.out.println("Your code is incorrect.");
        }

    }

    private void updateAppointmentStatus(AppointmentResponse appointmentResponse) throws IOException, InterruptedException, URISyntaxException, WhatsAppMessageException {
        log.atDebug().log("Updating appointment: "+appointmentResponse.getAppointmentId());
    }


}
