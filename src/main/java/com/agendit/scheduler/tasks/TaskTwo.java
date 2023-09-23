package com.agendit.scheduler.tasks;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import java.time.Duration;

import static java.time.Duration.ofMillis;

public class TaskTwo implements Tasklet {

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("MyTaskTwo start..");

        // ... your code
        Thread.sleep(ofMillis(10));
        System.out.println("MyTaskTwo done..");
        return RepeatStatus.FINISHED;
    }
}