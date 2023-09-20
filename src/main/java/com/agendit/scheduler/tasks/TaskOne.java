package com.agendit.scheduler.tasks;
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
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class TaskOne implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("MyTaskOne start..");

        // ... your code, whatsapp, email, etc
        /*
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://graph.facebook.com/v13.0/128009207057200/messages"))
                    .header("Authorization", "Bearer EAAVC7POMSAQBO7IrDeUiZAOI113B51zbbYraDig0mmnrSzzpDiSlzUGGb6D6bL0CozrBBBXeylBwCEHi0J50MzBidTfaffbTMQIItdZByQUZB04eVXZA0PbDHOw0uuLfErb9JgI0H44ZBqGfD2Sk5GARrZCiPoVPJ2Q7eU3x76vooZCAUbFPqscqQ2kDGupxI6dyjPZAEBe41qAjalBpTpKztYZCeGlcZD")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{ \"messaging_product\": \"whatsapp\", \"to\": \"595971673771\", \"type\": \"template\", \"template\": { \"name\": \"hello_world\", \"language\": { \"code\": \"en_US\" } } }"))
                    .build();
            HttpClient http = HttpClient.newHttpClient();
            HttpResponse<String> response = http.send(request,BodyHandlers.ofString());
            System.out.println(response.body());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

         */
        Thread.sleep(Duration.ofMillis(10));
        System.out.println("MyTaskOne done..");
        return RepeatStatus.FINISHED;
    }
}
