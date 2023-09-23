package com.agendit.scheduler.config;

import com.agendit.scheduler.mapper.MapStructMapper;
import com.agendit.scheduler.tasks.TaskTwo;
import com.agendit.scheduler.tasks.WhatsAppReminder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Step stepOne(JobRepository jobRepository, PlatformTransactionManager transactionManager, MapStructMapper mapStructMapper){
        return new StepBuilder("stepOne", jobRepository).tasklet(new WhatsAppReminder(mapStructMapper), transactionManager).build();
    }

    @Bean
    public Step stepTwo(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("stepTwo", jobRepository).tasklet(new TaskTwo(), transactionManager).build();
    }

    @Bean(name="demoJobOne")
    public Job demoJobOne(JobRepository jobRepository, PlatformTransactionManager transactionManager, MapStructMapper mapStructMapper){
        return new JobBuilder("demoJobOne", jobRepository)
                .start(stepOne(jobRepository, transactionManager, mapStructMapper))
//                .next(stepTwo(jobRepository, transactionManager))
                .build();
    }
}